package com.github.dozzatq.phoenix.auth;

/**
 * Created by Rodion Bartoshyk on 21.05.2017.
 */

public class PhoenixUser {
    private String middleName;
    private String familyName;
    private String personalName;
    private String phoneNumber;
    private String avatarUrl;
    private String thumbnailAvatarUrl;
    private String takenName;
    private Long lastSeenTime;
    private Long signUpTime;

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getThumbnailAvatarUrl() {
        return thumbnailAvatarUrl;
    }

    public void setThumbnailAvatarUrl(String thumbnailAvatarUrl) {
        this.thumbnailAvatarUrl = thumbnailAvatarUrl;
    }

    public String getTakenName() {
        return takenName;
    }

    public void setTakenName(String takenName) {
        this.takenName = takenName;
    }

    public Long getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(Long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public Long getSignUpTime() {
        return signUpTime;
    }

    public void setSignUpTime(Long signUpTime) {
        this.signUpTime = signUpTime;
    }
}
