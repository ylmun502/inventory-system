package com.daidaisuki.inventory.user;

public class AppSession {
  private static AppSession instance;
  private int userId;
  private String userName;

  private AppSession() {}

  public static AppSession getInstance() {
    if (instance == null) {
      instance = new AppSession();
    }
    return instance;
  }

  public void login(int userId, String userName) {
    this.userId = userId;
    this.userName = userName;
  }

  public int getUserId() {
    return this.userId;
  }

  public String getUserName() {
    return this.userName;
  }
}
