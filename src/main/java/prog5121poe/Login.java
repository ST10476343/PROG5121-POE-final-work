package prog5121poe;

import java.util.regex.Pattern;

public class Login {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final String cellPhone;

    public Login(String firstName, String lastName, String username, String password, String cellPhone) {
        this.firstName = firstName == null ? "" : firstName;
        this.lastName = lastName == null ? "" : lastName;
        this.username = username == null ? "" : username;
        this.password = password == null ? "" : password;
        this.cellPhone = cellPhone == null ? "" : cellPhone;
    }

    public boolean checkUserName() { return username.contains("_") && username.length() <= 5; }

    public boolean checkPasswordComplexity() {
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        return Pattern.matches(regex, password);
    }

    public boolean checkCellPhoneNumber() { return Pattern.matches("^\\+27\\d{9}$", cellPhone); }

    public String registerUser() {
        if (!checkUserName())
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        if (!checkPasswordComplexity())
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        if (!checkCellPhoneNumber())
            return "Cell phone number incorrectly formatted or does not contain an international code; please correct the number and try again.";
        return "User has been registered successfully.";
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        return loginUser(enteredUsername, enteredPassword)
                ? "Welcome " + firstName + " " + lastName + ", it is great to see you again."
                : "Username or password incorrect, please try again.";
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCellPhone() { return cellPhone; }
}
