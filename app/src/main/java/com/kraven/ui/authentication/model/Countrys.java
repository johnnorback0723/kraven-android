package com.kraven.ui.authentication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Countrys implements Parcelable {
    /**
     * Name of country
     */
    private String name;
    /**
     * ISO2 of country
     */
    private String iso;
    /**
     * Dial code prefix of country
     */
    private int dialCode;


    /**
     * Constructor
     *
     * @param name     String
     * @param iso      String of ISO2
     * @param dialCode int
     */
    public Countrys(String name, String iso, int dialCode) {
        setName(name);
        setIso(iso);
        setDialCode(dialCode);
    }

    protected Countrys(Parcel in) {
        name = in.readString();
        iso = in.readString();
        dialCode = in.readInt();
    }

    public static final Creator<Countrys> CREATOR = new Creator<Countrys>() {
        @Override
        public Countrys createFromParcel(Parcel in) {
            return new Countrys(in);
        }

        @Override
        public Countrys[] newArray(int size) {
            return new Countrys[size];
        }
    };

    /**
     * Get name of country
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of country
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get ISO2 of country
     *
     * @return String
     */
    public String getIso() {
        return iso;
    }

    /**
     * Set ISO2 of country
     *
     * @param iso String
     */
    public void setIso(String iso) {
        this.iso = iso.toUpperCase();
    }

    /**
     * Get dial code prefix of country (like +1)
     *
     * @return int
     */
    public int getDialCode() {
        return dialCode;
    }

    /**
     * Set dial code prefix of country (like +1)
     *
     * @param dialCode int (without + prefix!)
     */
    public void setDialCode(int dialCode) {
        this.dialCode = dialCode;
    }

    /**
     * Check if equals
     *
     * @param o Object to compare
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof Countrys) && (((Countrys) o).getIso().toUpperCase().equals(this.getIso().toUpperCase()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(iso);
        dest.writeInt(dialCode);
    }
}
