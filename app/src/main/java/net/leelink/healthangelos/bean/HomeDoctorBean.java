package net.leelink.healthangelos.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeDoctorBean implements Parcelable {


    /**
     * careDoctorRegedit : {"id":14,"loginName":"18222728241","password":"ec443a170ff5c28a0e7f693082149c84","salt":"f030ed229b4441789200784a725d06d9","organId":3,"hospitalId":6,"doctorTypeId":0,"shengId":120000,"shiId":120100,"xianId":120101,"doctorNo":"L03Y000014","doctorNoValue":14,"name":"○松","imgName":"","imgPath":"/img/86676370700248948d385458a443d9bb.png","physicianImgName":"","physicianImgPath":"/img/bedf42a67c924474899778a68690cde3.png","titleImgName":"","titleImgPath":"/img/205a9d206b1843adb7b813718eaf4ccf.png","age":0,"sex":0,"idNumber":"","nation":"","telephone":"152265696","regiseStatus":2,"hospital":"国际医院","department":"影像经验科","duties":"主任医师","title":"主任医师","visit":0,"percen":5,"startTime":"2020-10-23","endTime":"2020-10-23","address":"未认证(空)","skill":"研究电影","workHistory":"9秒","education":"未认证(空)","honor":"很强","createBy":1,"createTime":"2020-10-23 10:14:13","updateBy":3,"updateTime":"2020-11-27 11:00:14","regeditMethod":2,"doctorCertificateNo":"未认证(空)","diplomaNo":"未认证(空)","registOrgan":"乐聆智慧养老","phonePrice":0,"imgPrice":0.01,"wallet":0.02,"totalScore":0,"totalCount":0,"cardPositivePath":"/img/09238156d88042f79c17547e8795d6e4.png","cardBackPath":"/img/45b02561123a43009b0493825f5e8186.png","tagImgPath":"/img/2cbc8bd0f35c487980ebea7451aee7b6.png","diplomaImgPath":"/img/ca47e913a5b34a3686d956ceb6103905.png","imgState":1,"phoneState":1,"homeState":0,"hospitalState":1}
     * followCount : 0
     * consCount : 0
     * clientId : 3
     */

    private CareDoctorRegeditBean careDoctorRegedit;
    private int followCount;
    private int consCount;
    private String clientId;

    protected HomeDoctorBean(Parcel in) {
        followCount = in.readInt();
        consCount = in.readInt();
        clientId = in.readString();
    }

    public static final Creator<HomeDoctorBean> CREATOR = new Creator<HomeDoctorBean>() {
        @Override
        public HomeDoctorBean createFromParcel(Parcel in) {
            return new HomeDoctorBean(in);
        }

        @Override
        public HomeDoctorBean[] newArray(int size) {
            return new HomeDoctorBean[size];
        }
    };

    public CareDoctorRegeditBean getCareDoctorRegedit() {
        return careDoctorRegedit;
    }

    public void setCareDoctorRegedit(CareDoctorRegeditBean careDoctorRegedit) {
        this.careDoctorRegedit = careDoctorRegedit;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getConsCount() {
        return consCount;
    }

    public void setConsCount(int consCount) {
        this.consCount = consCount;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(followCount);
        dest.writeInt(consCount);
        dest.writeString(clientId);
    }

    public static class CareDoctorRegeditBean implements Parcelable {
        /**
         * id : 14
         * loginName : 18222728241
         * password : ec443a170ff5c28a0e7f693082149c84
         * salt : f030ed229b4441789200784a725d06d9
         * organId : 3
         * hospitalId : 6
         * doctorTypeId : 0
         * shengId : 120000
         * shiId : 120100
         * xianId : 120101
         * doctorNo : L03Y000014
         * doctorNoValue : 14
         * name : ○松
         * imgName :
         * imgPath : /img/86676370700248948d385458a443d9bb.png
         * physicianImgName :
         * physicianImgPath : /img/bedf42a67c924474899778a68690cde3.png
         * titleImgName :
         * titleImgPath : /img/205a9d206b1843adb7b813718eaf4ccf.png
         * age : 0
         * sex : 0
         * idNumber :
         * nation :
         * telephone : 152265696
         * regiseStatus : 2
         * hospital : 国际医院
         * department : 影像经验科
         * duties : 主任医师
         * title : 主任医师
         * visit : 0
         * percen : 5
         * startTime : 2020-10-23
         * endTime : 2020-10-23
         * address : 未认证(空)
         * skill : 研究电影
         * workHistory : 9秒
         * education : 未认证(空)
         * honor : 很强
         * createBy : 1
         * createTime : 2020-10-23 10:14:13
         * updateBy : 3
         * updateTime : 2020-11-27 11:00:14
         * regeditMethod : 2
         * doctorCertificateNo : 未认证(空)
         * diplomaNo : 未认证(空)
         * registOrgan : 乐聆智慧养老
         * phonePrice : 0
         * imgPrice : 0.01
         * wallet : 0.02
         * totalScore : 0
         * totalCount : 0
         * cardPositivePath : /img/09238156d88042f79c17547e8795d6e4.png
         * cardBackPath : /img/45b02561123a43009b0493825f5e8186.png
         * tagImgPath : /img/2cbc8bd0f35c487980ebea7451aee7b6.png
         * diplomaImgPath : /img/ca47e913a5b34a3686d956ceb6103905.png
         * imgState : 1
         * phoneState : 1
         * homeState : 0
         * hospitalState : 1
         */

        private String id;
        private String loginName;
        private String password;
        private String salt;
        private int organId;
        private int hospitalId;
        private int doctorTypeId;
        private int shengId;
        private int shiId;
        private int xianId;
        private String doctorNo;
        private int doctorNoValue;
        private String name;
        private String imgName;
        private String imgPath;
        private String physicianImgName;
        private String physicianImgPath;
        private String titleImgName;
        private String titleImgPath;
        private int age;
        private int sex;
        private String idNumber;
        private String nation;
        private String telephone;
        private int regiseStatus;
        private String hospital;
        private String department;
        private String duties;
        private String title;
        private int visit;
        private String percen;
        private String startTime;
        private String endTime;
        private String address;
        private String skill;
        private String workHistory;
        private String education;
        private String honor;
        private int createBy;
        private String createTime;
        private int updateBy;
        private String updateTime;
        private int regeditMethod;
        private String doctorCertificateNo;
        private String diplomaNo;
        private String registOrgan;
        private String phonePrice;
        private String imgPrice;
        private String wallet;
        private String totalScore;
        private int totalCount;
        private String cardPositivePath;
        private String cardBackPath;
        private String tagImgPath;
        private String diplomaImgPath;
        private int imgState;
        private int phoneState;
        private int homeState;
        private int hospitalState;

        protected CareDoctorRegeditBean(Parcel in) {
            id = in.readString();
            loginName = in.readString();
            password = in.readString();
            salt = in.readString();
            organId = in.readInt();
            hospitalId = in.readInt();
            doctorTypeId = in.readInt();
            shengId = in.readInt();
            shiId = in.readInt();
            xianId = in.readInt();
            doctorNo = in.readString();
            doctorNoValue = in.readInt();
            name = in.readString();
            imgName = in.readString();
            imgPath = in.readString();
            physicianImgName = in.readString();
            physicianImgPath = in.readString();
            titleImgName = in.readString();
            titleImgPath = in.readString();
            age = in.readInt();
            sex = in.readInt();
            idNumber = in.readString();
            nation = in.readString();
            telephone = in.readString();
            regiseStatus = in.readInt();
            hospital = in.readString();
            department = in.readString();
            duties = in.readString();
            title = in.readString();
            visit = in.readInt();
            percen = in.readString();
            startTime = in.readString();
            endTime = in.readString();
            address = in.readString();
            skill = in.readString();
            workHistory = in.readString();
            education = in.readString();
            honor = in.readString();
            createBy = in.readInt();
            createTime = in.readString();
            updateBy = in.readInt();
            updateTime = in.readString();
            regeditMethod = in.readInt();
            doctorCertificateNo = in.readString();
            diplomaNo = in.readString();
            registOrgan = in.readString();
            phonePrice = in.readString();
            imgPrice = in.readString();
            wallet = in.readString();
            totalScore = in.readString();
            totalCount = in.readInt();
            cardPositivePath = in.readString();
            cardBackPath = in.readString();
            tagImgPath = in.readString();
            diplomaImgPath = in.readString();
            imgState = in.readInt();
            phoneState = in.readInt();
            homeState = in.readInt();
            hospitalState = in.readInt();
        }

        public static final Creator<CareDoctorRegeditBean> CREATOR = new Creator<CareDoctorRegeditBean>() {
            @Override
            public CareDoctorRegeditBean createFromParcel(Parcel in) {
                return new CareDoctorRegeditBean(in);
            }

            @Override
            public CareDoctorRegeditBean[] newArray(int size) {
                return new CareDoctorRegeditBean[size];
            }
        };

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public int getOrganId() {
            return organId;
        }

        public void setOrganId(int organId) {
            this.organId = organId;
        }

        public int getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(int hospitalId) {
            this.hospitalId = hospitalId;
        }

        public int getDoctorTypeId() {
            return doctorTypeId;
        }

        public void setDoctorTypeId(int doctorTypeId) {
            this.doctorTypeId = doctorTypeId;
        }

        public int getShengId() {
            return shengId;
        }

        public void setShengId(int shengId) {
            this.shengId = shengId;
        }

        public int getShiId() {
            return shiId;
        }

        public void setShiId(int shiId) {
            this.shiId = shiId;
        }

        public int getXianId() {
            return xianId;
        }

        public void setXianId(int xianId) {
            this.xianId = xianId;
        }

        public String getDoctorNo() {
            return doctorNo;
        }

        public void setDoctorNo(String doctorNo) {
            this.doctorNo = doctorNo;
        }

        public int getDoctorNoValue() {
            return doctorNoValue;
        }

        public void setDoctorNoValue(int doctorNoValue) {
            this.doctorNoValue = doctorNoValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImgName() {
            return imgName;
        }

        public void setImgName(String imgName) {
            this.imgName = imgName;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getPhysicianImgName() {
            return physicianImgName;
        }

        public void setPhysicianImgName(String physicianImgName) {
            this.physicianImgName = physicianImgName;
        }

        public String getPhysicianImgPath() {
            return physicianImgPath;
        }

        public void setPhysicianImgPath(String physicianImgPath) {
            this.physicianImgPath = physicianImgPath;
        }

        public String getTitleImgName() {
            return titleImgName;
        }

        public void setTitleImgName(String titleImgName) {
            this.titleImgName = titleImgName;
        }

        public String getTitleImgPath() {
            return titleImgPath;
        }

        public void setTitleImgPath(String titleImgPath) {
            this.titleImgPath = titleImgPath;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public String getNation() {
            return nation;
        }

        public void setNation(String nation) {
            this.nation = nation;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public int getRegiseStatus() {
            return regiseStatus;
        }

        public void setRegiseStatus(int regiseStatus) {
            this.regiseStatus = regiseStatus;
        }

        public String getHospital() {
            return hospital;
        }

        public void setHospital(String hospital) {
            this.hospital = hospital;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getDuties() {
            return duties;
        }

        public void setDuties(String duties) {
            this.duties = duties;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getVisit() {
            return visit;
        }

        public void setVisit(int visit) {
            this.visit = visit;
        }

        public String getPercen() {
            return percen;
        }

        public void setPercen(String percen) {
            this.percen = percen;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public String getWorkHistory() {
            return workHistory;
        }

        public void setWorkHistory(String workHistory) {
            this.workHistory = workHistory;
        }

        public String getEducation() {
            return education;
        }

        public void setEducation(String education) {
            this.education = education;
        }

        public String getHonor() {
            return honor;
        }

        public void setHonor(String honor) {
            this.honor = honor;
        }

        public int getCreateBy() {
            return createBy;
        }

        public void setCreateBy(int createBy) {
            this.createBy = createBy;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(int updateBy) {
            this.updateBy = updateBy;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getRegeditMethod() {
            return regeditMethod;
        }

        public void setRegeditMethod(int regeditMethod) {
            this.regeditMethod = regeditMethod;
        }

        public String getDoctorCertificateNo() {
            return doctorCertificateNo;
        }

        public void setDoctorCertificateNo(String doctorCertificateNo) {
            this.doctorCertificateNo = doctorCertificateNo;
        }

        public String getDiplomaNo() {
            return diplomaNo;
        }

        public void setDiplomaNo(String diplomaNo) {
            this.diplomaNo = diplomaNo;
        }

        public String getRegistOrgan() {
            return registOrgan;
        }

        public void setRegistOrgan(String registOrgan) {
            this.registOrgan = registOrgan;
        }

        public String getPhonePrice() {
            return phonePrice;
        }

        public void setPhonePrice(String phonePrice) {
            this.phonePrice = phonePrice;
        }

        public String getImgPrice() {
            return imgPrice;
        }

        public void setImgPrice(String imgPrice) {
            this.imgPrice = imgPrice;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }

        public String getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(String totalScore) {
            this.totalScore = totalScore;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public String getCardPositivePath() {
            return cardPositivePath;
        }

        public void setCardPositivePath(String cardPositivePath) {
            this.cardPositivePath = cardPositivePath;
        }

        public String getCardBackPath() {
            return cardBackPath;
        }

        public void setCardBackPath(String cardBackPath) {
            this.cardBackPath = cardBackPath;
        }

        public String getTagImgPath() {
            return tagImgPath;
        }

        public void setTagImgPath(String tagImgPath) {
            this.tagImgPath = tagImgPath;
        }

        public String getDiplomaImgPath() {
            return diplomaImgPath;
        }

        public void setDiplomaImgPath(String diplomaImgPath) {
            this.diplomaImgPath = diplomaImgPath;
        }

        public int getImgState() {
            return imgState;
        }

        public void setImgState(int imgState) {
            this.imgState = imgState;
        }

        public int getPhoneState() {
            return phoneState;
        }

        public void setPhoneState(int phoneState) {
            this.phoneState = phoneState;
        }

        public int getHomeState() {
            return homeState;
        }

        public void setHomeState(int homeState) {
            this.homeState = homeState;
        }

        public int getHospitalState() {
            return hospitalState;
        }

        public void setHospitalState(int hospitalState) {
            this.hospitalState = hospitalState;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(loginName);
            dest.writeString(password);
            dest.writeString(salt);
            dest.writeInt(organId);
            dest.writeInt(hospitalId);
            dest.writeInt(doctorTypeId);
            dest.writeInt(shengId);
            dest.writeInt(shiId);
            dest.writeInt(xianId);
            dest.writeString(doctorNo);
            dest.writeInt(doctorNoValue);
            dest.writeString(name);
            dest.writeString(imgName);
            dest.writeString(imgPath);
            dest.writeString(physicianImgName);
            dest.writeString(physicianImgPath);
            dest.writeString(titleImgName);
            dest.writeString(titleImgPath);
            dest.writeInt(age);
            dest.writeInt(sex);
            dest.writeString(idNumber);
            dest.writeString(nation);
            dest.writeString(telephone);
            dest.writeInt(regiseStatus);
            dest.writeString(hospital);
            dest.writeString(department);
            dest.writeString(duties);
            dest.writeString(title);
            dest.writeInt(visit);
            dest.writeString(percen);
            dest.writeString(startTime);
            dest.writeString(endTime);
            dest.writeString(address);
            dest.writeString(skill);
            dest.writeString(workHistory);
            dest.writeString(education);
            dest.writeString(honor);
            dest.writeInt(createBy);
            dest.writeString(createTime);
            dest.writeInt(updateBy);
            dest.writeString(updateTime);
            dest.writeInt(regeditMethod);
            dest.writeString(doctorCertificateNo);
            dest.writeString(diplomaNo);
            dest.writeString(registOrgan);
            dest.writeString(phonePrice);
            dest.writeString(imgPrice);
            dest.writeString(wallet);
            dest.writeString(totalScore);
            dest.writeInt(totalCount);
            dest.writeString(cardPositivePath);
            dest.writeString(cardBackPath);
            dest.writeString(tagImgPath);
            dest.writeString(diplomaImgPath);
            dest.writeInt(imgState);
            dest.writeInt(phoneState);
            dest.writeInt(homeState);
            dest.writeInt(hospitalState);
        }
    }
}
