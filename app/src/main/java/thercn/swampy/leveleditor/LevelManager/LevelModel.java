/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package thercn.swampy.leveleditor.LevelManager;

public class LevelModel {
  private String levelId = "";
  private String levelName = "";
  private String numberOfLikes = "";
  private String packageId = "";
  private String packageName = "";
  private String userLikes = "";

  public LevelModel() {}

  public LevelModel(String string2, String string3, String string4,
                    String string5, String string6, String string7) {
    this.levelId = string2;
    this.packageId = string3;
    this.levelName = string4;
    this.packageName = string5;
    this.numberOfLikes = string6;
    this.userLikes = string7;
  }

  public String getLevelId() { return this.levelId; }

  public String getLevelName() { return this.levelName; }

  public String getNumberOfLikes() { return this.numberOfLikes; }

  public String getPackageId() { return this.packageId; }

  public String getPackageName() { return this.packageName; }

  public String getUserLikes() { return this.userLikes; }

  public void setLevelId(String string2) { this.levelId = string2; }

  public void setLevelName(String string2) { this.levelName = string2; }

  public void setNumberOfLikes(String string2) { this.numberOfLikes = string2; }

  public void setPackageId(String string2) { this.packageId = string2; }

  public void setPackageName(String string2) { this.packageName = string2; }

  public void setUserLikes(String string2) { this.userLikes = string2; }
}
