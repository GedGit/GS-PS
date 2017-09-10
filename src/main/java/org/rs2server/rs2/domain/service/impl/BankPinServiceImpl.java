package org.rs2server.rs2.domain.service.impl;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.joda.time.*;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.content.api.bank.BankPinEvent;
import org.rs2server.rs2.content.api.bank.BankSettingsClickEvent;
import org.rs2server.rs2.domain.model.player.PlayerBankEntity;
import org.rs2server.rs2.domain.model.player.PlayerSettingsEntity;
import org.rs2server.rs2.domain.service.api.BankPinService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

/**
 * A service implementation for bank pins.
 *
 * @author twelve
 */
public final class BankPinServiceImpl implements BankPinService {

    /**
     * The bank pin widget id.
     */
    public static final int BANK_PIN_WIDGET = 213;

    /**
     * The bank settings widget id
     */
    public static final int BANK_SETTINGS_WIDGET = 14;

    /**
     * The widget child id of the bank name text.
     */
    private static final int BANK_TEXT_CHILD = 2;
    /**
     * The widget child id of message requesting you to enter your pin.
     */
    private static final int PIN_TEXT_CHILD = 7;

    private static final int PIN_TEXT_TITLE = 2;
    /**
     * The widget child id of the recovery delay text.
     */
    private static final int RECOVERY_DELAY_CHILD = 8;

    /**
     * The message displayed when an incorrect pin is entered.
     */
    private static final String INCORRECT_PIN = "<col=7f0000>That number was incorrect.<br>Please try again.";

    /**
     * Text displayed on the pin widget requesting a user to enter their code. displayed on {@code PIN_TEXT_CHILD}
     */
    private static final String PLEASE_ENTER_PIN = "Please enter your PIN using the buttons below";

    private static final String CHOOSE_NEW_PIN = "Choose new PIN";
    private static final String FOUR_DIGIT_PIN = "Please choose a new FOUR DIGIT PIN using the buttons below.";
    private static final String PIN_EXISTS = "";
    private static final String CHANGE_PIN = "Do you really wish to change your bank PIN?";
    private static final String DELETE_PIN = "Do you really wish to delete your bank PIN?";
    private static final String READY_FOR_NEW = "Yes, I am ready for a new one.";
    private static final String EXTRA_SECURITY = "No thanks, I'd rather keep the extra security.";
    private static final String KEEP_PRESENT_PIN = "No thanks, I'll stick with my present one.";
    private static final String GET_RID_OF_PIN = "Yes, I don't need a PIN anymore.";
    private static final String PLEASE_ENTER_AGAIN = "Now please enter that number again.";
    private static final String CONFIRM_NEW_PIN = "Confirm new PIN";
    /**
     * Text displayed in the messages box when opening bank pin settings
     */
    private static final String DEFAULT_MESSAGE = "Customers are reminded that they should NEVER tell anyone their Bank PINs or passwords, nor should they ever enter their PINs on any website form.<br><br>Have you read the PIN guide on the website?";
    private static final String PIN_SET_MESSAGE = "Your PIN has been changed to the number you entered.<br><br>This takes effect immediately.<br><br>If you cannot remember your new PIN, you should delete it now.";
    /**
     * Text displayed if you have a bank pin set
     */
    private static final String PIN_SET = "You have a PIN";
    /**
     * Text displayed if you do not have a bank pin set
     */
    private static final String NO_PIN_SET = "No PIN set";
    private static final String PIN_COMING_SOON = "PIN coming soon";
    /**
     * The string to display on {@code BANK_TEXT_CHILD}
     */
    private static final String BANK_NAME = "Bank of "+Constants.SERVER_NAME;

    public enum SettingScreenType {
        DEFAULT(DEFAULT_MESSAGE),
        PIN_SET(PIN_SET_MESSAGE);

        private final String message;

        SettingScreenType(String message) {
            this.message = message;
        }

        public final String getMessage() {
            return message;
        }
    }

    public enum ConfirmationType {
        MODIFY(CHANGE_PIN, READY_FOR_NEW, KEEP_PRESENT_PIN),
        DELETE(DELETE_PIN, GET_RID_OF_PIN, EXTRA_SECURITY);

        private final String title;
        private final String yesOption;
        private final String noOption;

        ConfirmationType(String title, String yesOption, String noOption) {
            this.title = title;
            this.yesOption = yesOption;
            this.noOption = noOption;
        }

        public String getTitle() {
            return title;
        }

        public String getYesOption() {
            return yesOption;
        }

        public String getNoOption() {
            return noOption;
        }
    }

    public enum PinType {
        NEW(CHOOSE_NEW_PIN, FOUR_DIGIT_PIN),
        EXISTING(PIN_EXISTS, PLEASE_ENTER_PIN), CONFIRM_PIN(CONFIRM_NEW_PIN, PLEASE_ENTER_AGAIN);


        private final String title;
        private final String buttonString;

        PinType(String title, String buttonString) {
            this.title = title;
            this.buttonString = buttonString;
        }

        public String getTitle() {
            return title;
        }

        public String getButtonString() {
            return buttonString;
        }
    }

    @Inject
    public BankPinServiceImpl(HookService service) {
        service.register(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void openPinInterface(@Nonnull Player player, PinType type) {
        player.getActionSender().sendString(BANK_PIN_WIDGET, BANK_TEXT_CHILD, BANK_NAME)
                .sendString(BANK_PIN_WIDGET, PIN_TEXT_CHILD, type.getButtonString())
                .sendString(BANK_PIN_WIDGET, PIN_TEXT_TITLE, type.getTitle())
                .sendInterfaceConfig(BANK_PIN_WIDGET, BANK_SETTINGS_WIDGET, false)
                .sendInterface(BANK_PIN_WIDGET, false);
        player.setAttribute("pinType", type);
    }

    @Override
    public final void openPinSettingsInterface(@Nonnull Player player, SettingScreenType type) {


        PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
        PlayerBankEntity bank = player.getDatabaseEntity().getBank();
        boolean secured = settings.isBankSecured();
        int recoveryDelay = bank.getRecoveryDelay();

        if (secured && !player.isEnteredPinOnce()) {
            openPinInterface(player, BankPinServiceImpl.PinType.EXISTING);
            return;
        }

        if (bank.getRequestedPin() != null) {
            DateTime time = bank.getPinRequestTime().plus(Duration.standardDays(recoveryDelay));
            DateTime now = DateTime.now(DateTimeZone.UTC);
            Duration remainingDuration = Duration.millis(time.getMillis() - now.getMillis());

            long hours = remainingDuration.minus(Days.days((int) remainingDuration.getStandardDays()).toStandardDuration().getMillis()).getStandardHours();
            long minutes = remainingDuration.minus(Days.days((int) remainingDuration.getStandardDays()).toStandardDuration()).minus(Hours.hours((int) hours).toStandardDuration()).getStandardMinutes();
            String message = "You have requested that a PIN be set on your bank account. This will take effect in " + remainingDuration.getStandardDays() + " days, " + hours + " hours, " + minutes + " minutes.<br><br>If you wish to cancel this PIN, please use the button on the left.";
            player.getActionSender().sendString(BANK_SETTINGS_WIDGET, 6, PIN_COMING_SOON)
                    .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 16, true)
                    .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 19, true)
                    .sendString(BANK_SETTINGS_WIDGET, 12, message).
                    sendInterfaceConfig(BANK_SETTINGS_WIDGET, 23, false);
        } else {
            player.getActionSender().sendString(BANK_SETTINGS_WIDGET, 6, secured ? PIN_SET : NO_PIN_SET).sendInterfaceConfig(BANK_SETTINGS_WIDGET, 16, secured)
                    .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 19, !secured)
                    .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 23, true)
                    .sendString(BANK_SETTINGS_WIDGET, 12, type.getMessage());
        }

        player.getActionSender()
                .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 0, false)
                .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 25, true)
                .sendString(BANK_SETTINGS_WIDGET, RECOVERY_DELAY_CHILD, recoveryDelay + " days")
                .sendInterface(BANK_SETTINGS_WIDGET, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void pinFailed(@Nonnull Player player) {
        onClose(player);
        player.getActionSender().removeInterface()
                .sendCS2Script(600, new Object[]{15007744, 31, 1, 1}, "iiil")
                .sendAccessMask(1, 229, 1, -1, -1)
                .sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null, INCORRECT_PIN);
    }

    @Override
    public final void openConfirmationInterface(@Nonnull Player player, ConfirmationType type) {
        player.getActionSender()
                .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 0, true)
                .sendInterfaceConfig(BANK_SETTINGS_WIDGET, 25, false)
                .sendString(BANK_SETTINGS_WIDGET, 27, type.getTitle())
                .sendString(BANK_SETTINGS_WIDGET, 30, type.getYesOption())
                .sendString(BANK_SETTINGS_WIDGET, 33, type.getNoOption())
                .sendInterface(BANK_SETTINGS_WIDGET, false);
        player.setAttribute("pinConfirmationType", type);
    }

    @Override
    public final void onClose(@Nonnull Player player) {
        player.setTemporaryPin(null);
        player.removeAttribute("pinType");
        player.removeAttribute("pinConfirmationType");
        player.removeAttribute("confirmType");
    }

    /**
     * Executed when an {@link BankPinEvent} is posted to the event bus.
     *
     * @param event The posted event.
     */

    @Subscribe
    public final void onBankPinReceived(@Nonnull BankPinEvent event) {
        Player player = event.getPlayer();
        PinType type = player.getAttribute("pinType");
        PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
        PlayerBankEntity bank = player.getDatabaseEntity().getBank();
        int[] tempPin = player.getTemporaryPin();
        int[] currentPin = IntStream.of(settings.getBankPinDigit1(), settings.getBankPinDigit2(), settings.getBankPinDigit3(), settings.getBankPinDigit4()).toArray();
        int[] digits = String.valueOf(event.getPin()).chars().map(c -> Character.digit(c, 10)).toArray();

        if (digits.length != 4) {
            onClose(player);
            return;
        }

        if (type == PinType.NEW) {
            player.setTemporaryPin(digits);
            openPinInterface(player, PinType.CONFIRM_PIN);
        } else if (type == PinType.CONFIRM_PIN) {

            for (int i = 0; i < tempPin.length; i++) {
                if (tempPin[i] != digits[i]) {
                    pinFailed(player);
                    return;
                }
            }
            player.setTemporaryPin(null);

            if (bank.isDeleted()) {
                bank.setDeleted(false);
                if (bank.getPinRequestTime() == null) {
                    bank.setPinRequestTime(DateTime.now());
                }
                bank.setRequestedPin(tempPin);
                openPinSettingsInterface(player, SettingScreenType.PIN_SET);
            } else {
                DateTime requestTime = bank.getPinRequestTime();
                DateTime now = DateTime.now(DateTimeZone.UTC);
                if (requestTime == null || bank.getPinRequestTime().plus(Duration.standardDays(bank.getRecoveryDelay())).isBefore(now)) {
                    settings.setBankPinDigit1(tempPin[0]);
                    settings.setBankPinDigit2(tempPin[1]);
                    settings.setBankPinDigit3(tempPin[2]);
                    settings.setBankPinDigit4(tempPin[3]);
                    settings.setBankSecured(true);
                    player.setEnteredPinOnce(true);
                    openPinSettingsInterface(player, SettingScreenType.PIN_SET);
                } else {
                    openPinSettingsInterface(player, SettingScreenType.DEFAULT);
                }
            }
        } else if (type == PinType.EXISTING) {
            ConfirmationType confirmationType = player.getAttribute("confirmType");

            for (int i = 0; i < digits.length; i++) {
                if (currentPin[i] != digits[i]) {
                    pinFailed(player);
                    return;
                }
            }
            if (confirmationType != null && confirmationType == ConfirmationType.DELETE) {
                settings.setBankPinDigit1(0);
                settings.setBankPinDigit2(0);
                settings.setBankPinDigit3(0);
                settings.setBankPinDigit4(0);
                settings.setBankSecured(false);
                bank.setDeleted(true);
                openPinSettingsInterface(player, SettingScreenType.DEFAULT);
                player.removeAttribute("confirmType");
            } else {
                player.setEnteredPinOnce(true);
                Bank.open(player);
            }
        }
    }

    /**
     * Executed when an {@link BankSettingsClickEvent} is posted to the event bus
     *
     * @param event The posted event.
     */
    @Subscribe
    public final void onBankSettingsClickReceived(@Nonnull BankSettingsClickEvent event) {
        Player player = event.getPlayer();
        int child = event.getChild();

        ConfirmationType type = player.getAttribute("pinConfirmationType");
        PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
        PlayerBankEntity bank = player.getDatabaseEntity().getBank();

        if (type != null) {
            if (child == 33) {
                openPinSettingsInterface(player, SettingScreenType.DEFAULT);
            } else if (child == 30) {
                if (type == ConfirmationType.DELETE) {
                    settings.setBankPinDigit1(0);
                    settings.setBankPinDigit2(0);
                    settings.setBankPinDigit3(0);
                    settings.setBankPinDigit4(0);
                    settings.setBankSecured(false);
                    bank.setDeleted(true);

                    openPinSettingsInterface(player, SettingScreenType.DEFAULT);
                } else {
                    openPinInterface(player, PinType.NEW);
                }
            }

            if (child == 30 || child == 33) {
                player.removeAttribute("pinConfirmationType");
                player.setAttribute("confirmType", type);
            }
        } else {
            if (child == 17 || child == 20) {
                openConfirmationInterface(player, ConfirmationType.MODIFY);
            } else if (child == 21) {
                openConfirmationInterface(player, ConfirmationType.DELETE);
            } else if (child == 22) {
                openConfirmationInterface(player, ConfirmationType.DELETE);
            } else if (child == 24) {
                bank.setDeleted(true);
                bank.setRequestedPin(null);
                openPinSettingsInterface(player, SettingScreenType.DEFAULT);
            } else if (child == 18) {

                int days = bank.getRecoveryDelay();

                if (days == 7) {
                    bank.setRecoveryDelay(3);
                } else if (days == 3) {
                    bank.setRecoveryDelay(7);
                }
                openPinSettingsInterface(player, SettingScreenType.DEFAULT);
            }
        }
    }

}
