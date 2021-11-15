package com.example.test.activity.model;

import java.util.List;

public class UserInfoModel {

    /**
     * data : {"coinInfo":{"coinCount":870,"level":9,"nickname":"","rank":"1959","userId":76328,"username":"1**53461844"},"userInfo":{"admin":false,"chapterTops":[],"coinCount":870,"collectIds":[15222,15221,18453,19040,19035,17083,17131,18930,19180,19476,18414,2458,8652,18281,1165,2,20130,20087],"email":"739574055@qq.com","icon":"","id":76328,"nickname":"LFHQAQ","password":"","publicName":"LFHQAQ","token":"","type":0,"username":"17853461844"}}
     * errorCode : 0
     * errorMsg :
     */

    private DataBean data;
    private int errorCode;
    private String errorMsg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static class DataBean {
        /**
         * coinInfo : {"coinCount":870,"level":9,"nickname":"","rank":"1959","userId":76328,"username":"1**53461844"}
         * userInfo : {"admin":false,"chapterTops":[],"coinCount":870,"collectIds":[15222,15221,18453,19040,19035,17083,17131,18930,19180,19476,18414,2458,8652,18281,1165,2,20130,20087],"email":"739574055@qq.com","icon":"","id":76328,"nickname":"LFHQAQ","password":"","publicName":"LFHQAQ","token":"","type":0,"username":"17853461844"}
         */

        private CoinInfoBean coinInfo;
        private UserInfoBean userInfo;

        public CoinInfoBean getCoinInfo() {
            return coinInfo;
        }

        public void setCoinInfo(CoinInfoBean coinInfo) {
            this.coinInfo = coinInfo;
        }

        public UserInfoBean getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfoBean userInfo) {
            this.userInfo = userInfo;
        }

        public static class CoinInfoBean {
            /**
             * coinCount : 870
             * level : 9
             * nickname :
             * rank : 1959
             * userId : 76328
             * username : 1**53461844
             */

            private int coinCount;
            private int level;
            private String nickname;
            private String rank;
            private int userId;
            private String username;

            public int getCoinCount() {
                return coinCount;
            }

            public void setCoinCount(int coinCount) {
                this.coinCount = coinCount;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getRank() {
                return rank;
            }

            public void setRank(String rank) {
                this.rank = rank;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }

        public static class UserInfoBean {
            /**
             * admin : false
             * chapterTops : []
             * coinCount : 870
             * collectIds : [15222,15221,18453,19040,19035,17083,17131,18930,19180,19476,18414,2458,8652,18281,1165,2,20130,20087]
             * email : 739574055@qq.com
             * icon :
             * id : 76328
             * nickname : LFHQAQ
             * password :
             * publicName : LFHQAQ
             * token :
             * type : 0
             * username : 17853461844
             */

            private boolean admin;
            private int coinCount;
            private String email;
            private String icon;
            private int id;
            private String nickname;
            private String password;
            private String publicName;
            private String token;
            private int type;
            private String username;
            private List<?> chapterTops;
            private List<Integer> collectIds;

            public boolean isAdmin() {
                return admin;
            }

            public void setAdmin(boolean admin) {
                this.admin = admin;
            }

            public int getCoinCount() {
                return coinCount;
            }

            public void setCoinCount(int coinCount) {
                this.coinCount = coinCount;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPublicName() {
                return publicName;
            }

            public void setPublicName(String publicName) {
                this.publicName = publicName;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public List<?> getChapterTops() {
                return chapterTops;
            }

            public void setChapterTops(List<?> chapterTops) {
                this.chapterTops = chapterTops;
            }

            public List<Integer> getCollectIds() {
                return collectIds;
            }

            public void setCollectIds(List<Integer> collectIds) {
                this.collectIds = collectIds;
            }
        }
    }

    @Override
    public String toString() {
        return "UserInfoModel{" +
                "data=" + data +
                ", errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
