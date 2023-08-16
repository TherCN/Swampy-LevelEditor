/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package thercn.swampy.leveleditor.LevelManager;

public class LevelPackageModel {
    private String packageId = "";
    private String packageName = "";

    public LevelPackageModel() {
    }

    public LevelPackageModel(String string2, String string3) {
        this.packageId = string2;
        this.packageName = string3;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageId(String string2) {
        this.packageId = string2;
    }

    public void setPackageName(String string2) {
        this.packageName = string2;
    }
}

