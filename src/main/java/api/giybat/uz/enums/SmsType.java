package api.giybat.uz.enums;

/**
 * once given error in database when new enum type added to class then we can use this below
 *
 * select * from sms_history order by created_date desc;
 * alter table sms_history
 * drop constraint sms_history_sms_type_check;
 *
 *
 * */


public enum SmsType {
    REGISTRATION,
    RESET_PASSWORD,
    VERIFY,
    CONFIRM_RESET_PASSWORD,
    USERNAME_UPDATE

}
