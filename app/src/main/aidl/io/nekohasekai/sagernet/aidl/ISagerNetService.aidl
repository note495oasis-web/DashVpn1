package io.nekohasekai.sagernet.aidl;

import io.nekohasekai.sagernet.aidl.ISagerNetServiceCallback;

interface ISagerNetService {
  int getState();
  String getProfileName();
  long getConnectedAt();

  void registerCallback(in ISagerNetServiceCallback cb, int id);
  oneway void unregisterCallback(in ISagerNetServiceCallback cb);

  int urlTest();
}
