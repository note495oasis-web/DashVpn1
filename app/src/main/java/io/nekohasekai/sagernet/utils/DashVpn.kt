package io.nekohasekai.sagernet.utils

import android.util.Base64
import io.nekohasekai.sagernet.R
import java.net.URLDecoder

object DashVpn {

    data class ServerInfo(val name: String, val description: String?, val countryCode: String?)

    private const val DESCRIPTION_MARKER = "?serverDescription="

    fun parseRemark(raw: String): ServerInfo {
        var text = raw
        if (Regex("%[0-9A-Fa-f]{2}").containsMatchIn(text)) {
            text = try {
                URLDecoder.decode(text, "UTF-8")
            } catch (_: Exception) {
                text
            }
        }

        var description: String? = null
        val descIndex = text.indexOf(DESCRIPTION_MARKER)
        if (descIndex >= 0) {
            val encoded = text.substring(descIndex + DESCRIPTION_MARKER.length).substringBefore("&")
            description = try {
                String(Base64.decode(encoded, Base64.URL_SAFE), Charsets.UTF_8).trim()
            } catch (_: Exception) {
                encoded
            }
            text = text.substring(0, descIndex)
        }

        var countryCode: String? = null
        val cleaned = StringBuilder()
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            val count = Character.charCount(cp)
            if (cp in 0x1F1E6..0x1F1FF && i + count < text.length) {
                val cp2 = text.codePointAt(i + count)
                if (cp2 in 0x1F1E6..0x1F1FF) {
                    if (countryCode == null) {
                        countryCode = "${'A' + (cp - 0x1F1E6)}${'A' + (cp2 - 0x1F1E6)}"
                    }
                    i += count + Character.charCount(cp2)
                    continue
                }
            }
            if (!isEmoji(cp)) cleaned.appendCodePoint(cp)
            i += count
        }

        var name = cleaned.toString()
        name = name.replace(Regex("@[\\w.]+"), " ")
        name = name.replace(Regex("\\s+"), " ").trim()
        if (name.isEmpty()) name = "DashVPN"

        return ServerInfo(name, description?.takeIf { it.isNotEmpty() }, countryCode)
    }

    private fun isEmoji(cp: Int): Boolean =
        cp in 0x1F000..0x1FAFF ||
                cp in 0x2600..0x27BF ||
                cp in 0x2B00..0x2BFF ||
                cp in 0xFE00..0xFE0F ||
                cp in 0x2190..0x21FF ||
                cp == 0x200D || cp == 0x20E3 || cp == 0x2122 || cp == 0x00A9 || cp == 0x00AE

    fun flagRes(countryCode: String?): Int = when (countryCode?.uppercase()) {
        "NL" -> R.drawable.flag_nl
        "EU" -> R.drawable.flag_eu
        else -> R.drawable.flag_dv
    }

    fun protocolBadge(displayType: String): String = when {
        displayType.equals("VLESS", true) -> "VLESS"
        displayType.equals("VMess", true) -> "VMESS"
        displayType.startsWith("Hysteria2") -> "HY2"
        displayType.startsWith("Hysteria1") -> "HY1"
        displayType.startsWith("Hysteria") -> "HY"
        displayType.equals("Shadowsocks", true) -> "SS"
        displayType.equals("Trojan", true) -> "TROJAN"
        displayType.equals("Trojan-Go", true) -> "TROJAN"
        displayType.equals("TUIC", true) -> "TUIC"
        displayType.equals("WireGuard", true) -> "WG"
        displayType.equals("SSH", true) -> "SSH"
        displayType.equals("Naïve", true) -> "NAIVE"
        else -> displayType.uppercase()
    }
}