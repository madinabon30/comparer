package uz.fb.comparer.model;


public class ComparingModel {
    private String pinfl;
    private String phone;


    public String getPinfl() {
        return pinfl;
    }

    public void setPinfl(String pinfl) {
        this.pinfl = pinfl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String toString() {
        return "ComparingModel[" +
                "pinfl=" + pinfl + ", " +
                "phone=" + phone + ']';
    }
}
