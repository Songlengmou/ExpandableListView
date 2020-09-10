package com.anningtex.expandablelistview.entity;

/**
 * @Author Song
 */
public class MainFinishEntity {
    private int QBales;
    private String BGLID;
    private String PIID;

    public int getQBales() {
        return QBales;
    }

    public void setQBales(int QBales) {
        this.QBales = QBales;
    }

    public String getBGLID() {
        return BGLID;
    }

    public void setBGLID(String BGLID) {
        this.BGLID = BGLID;
    }

    public String getPIID() {
        return PIID;
    }

    public void setPIID(String PIID) {
        this.PIID = PIID;
    }

    @Override
    public String toString() {
        return "RemainingFinishEntity{" +
                "QBales=" + QBales +
                ", BGLID='" + BGLID + '\'' +
                ", PIID='" + PIID + '\'' +
                '}';
    }
}
