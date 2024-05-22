package site.soconsocon.utils;

import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor
public class MaskUtils {
    /**
     * korean name
     * - second letter of name
     * - if name includes more than four letter,
     * - than mask all except first and last one.
     * @param name
     * @return
     */
    public static String nameMaskKo(String name){
        String middleMask="";
        StringBuilder maskedName=new StringBuilder(name.substring(0,1));
        int nameLength=name.length();
        if(nameLength==2){
            maskedName.append("*");
        }else{
            middleMask=name.substring(1,nameLength-1);
            maskedName.append("*".repeat(middleMask.length()));
            maskedName.append(name.substring(nameLength-1));
        }
        return maskedName.toString();
    }

    /**
     * English name
     * includes two more words : mask all except first word
     * include only one word : mask last letter
     * @param name
     * @return
     */
    public static String nameMaskEng(String name){
        StringBuilder maskedName=new StringBuilder();
        String[] names=name.split("\\s+");
        System.out.println(Arrays.toString(names));
        if(names.length==1){
            maskedName.append(name.substring(0, name.length()-1));
            maskedName.append("*");
        }else{
            maskedName.append(names[0]);
            for (int i = 1; i < names.length; i++) {
                maskedName.append(" ").append("*".repeat(names[i].length()));
            }
        }
        return maskedName.toString();
    }

    /**
     * email
     * - includes five more letters : mask after first four letters
     * - others : mask all except first letter
     * @param email
     * @return
     */
    public static String emailMask(String email){
        String[] emailParts=email.split("@");
        StringBuilder maskedEmail=new StringBuilder();
        int emailLength=emailParts[0].length();
        if(emailLength<5){
            maskedEmail.append(emailParts[0].charAt(0));
            maskedEmail.append("*".repeat(emailLength-1));
        }else{
            maskedEmail.append(emailParts[0].substring(0,4));
            maskedEmail.append("*".repeat(emailLength-4));
        }
        maskedEmail.append("@");
        maskedEmail.append(emailParts[1]);
        return maskedEmail.toString();
    }

    /**
     * password
     * - mask all
     * @param password
     * @return
     */
    public static String passwordMask(String password){
        StringBuilder maskedPassword=new StringBuilder();
        maskedPassword.append("*".repeat(password.length()));
        return maskedPassword.toString();
    }

}

