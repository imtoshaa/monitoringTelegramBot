package by.telegram.monitoring.bot.telegram;

import by.telegram.monitoring.bot.entities.Day;
import by.telegram.monitoring.bot.entities.Event;
import by.telegram.monitoring.bot.entities.User;
import by.telegram.monitoring.bot.repositories.DayRepository;
import by.telegram.monitoring.bot.repositories.EventRepository;
import by.telegram.monitoring.bot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static by.telegram.monitoring.bot.utils.constants.TelegramConstants.*;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class MonitoringTelegramBot extends TelegramLongPollingBot {

    private final String botUsername = "@Mikashevichi_bot";
    private final String botToken = "5636190624:AAFQHb5EemguvCOy6bbKF3JcCkhdfLcslkI";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private PasswordEncoder encoder;


    private Event eventHolder;
    private int lastKeyboardId = 0;
    private String activeActon = "";
    private boolean isCreatingNewEvent;


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                handleCallback(update.getCallbackQuery());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage()) {
            try {
                if (userRepository.existsUserByChatId(update.getMessage().getChatId())) {
                    handleTelegramCommands(update.getMessage());
                    handleStringsFromStaticKeyboard(update.getMessage());
                    handleCommandsFromKeyboard(update.getMessage());
                    createGeneralStaticKeyboard(update.getMessage());
                } else {
                    execute(SendMessage.builder()
                            .chatId(update.getMessage().getChatId())
                            .text("???? ???? ????????????????????????????????. ???????????????? ??????????????????????.")
                            .build());
                    handleTelegramCommands(update.getMessage());
                }


            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleTelegramCommands(Message message) throws TelegramApiException {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntities =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntities.isPresent()) {
                String command = message.getText()
                        .substring(commandEntities.get().getOffset(), commandEntities.get().getLength());
                switch (command) {
                    case "/start" -> {
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text("""
                                        ?????? ???????????????????????? ????????????????-??????.
                                        ?????? ???????????????????????????? ?? ?????????? ???? ???????????? ???????????????????????? ?????????????????? ??????????????:
                                        /registration *?????????????????? ??????????* *???????????????? ????????????* - ?????????????????? ?????? ?? ????????????????
                                        ?????? ???????????????? ???? ???????? ?????????????? ???????????????? ?? ???????????????? ???????????? https://botmikashevichi.herokuapp.com/home
                                        """)
                                .build());
                    }
                    case "/registration" -> {
                        try {
                            List<String> messageParameters = Arrays.stream(message.getText().split(" ")).toList();
                            String serialNumber = messageParameters.get(1);
                            String password = messageParameters.get(2);
                            if (serialNumber != null && password != null) {
                                User user = userRepository.getUserBySerialNumber(serialNumber);
                                if (user != null) {
                                    user.setChatId(message.getChatId());
                                    user.setPassword(encoder.encode(password));
                                    userRepository.save(user);
                                    execute(SendMessage.builder()
                                            .chatId(message.getChatId())
                                            .text("???? ?????????????? ????????????????????????????????!")
                                            .build());
                                } else {
                                    execute(SendMessage.builder()
                                            .chatId(message.getChatId())
                                            .text("?????? ???? ???????????????????????? ???????????? ?? ?????????????? ????????!")
                                            .build());
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void handleStringsFromStaticKeyboard(Message message) throws TelegramApiException {
        if (message.hasText()) {
            switch (message.getText()) {
                case "?????????? ??????????????" -> {
                    eventHolder = new Event();
                    isCreatingNewEvent = true;

                    Date date = new Date();
                    SimpleDateFormat formattingForTime = new SimpleDateFormat("H:mm:ss");
                    SimpleDateFormat formattingForDay = new SimpleDateFormat("d.M.y");

                    String dateDay = formattingForDay.format(date);
//                    Day currentDay = Day.builder().date(dateDay).build();
                    Day currentDay = dayRepository.getDayByDate(dateDay);
                    if (currentDay == null) {
                        dayRepository.save(Day.builder().date(dateDay).build());
                    }
                    currentDay = dayRepository.getDayByDate(dateDay);

                    eventHolder.setTime(formattingForTime.format(date));
                    eventHolder.setDay(currentDay);
                    eventHolder.setUser(userRepository.getUserByChatId(message.getChatId()));
                    sendButtonsForCreatingEvent(message);
                }
                case "????????????" -> {
                    eventHolder = null;
                    isCreatingNewEvent = false;
                    activeActon = "";
                }
            }
        }
    }

    /**
     * ?????????????????? callback ?? ????????????????????
     */
    private void handleCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        String command = callbackQuery.getData();

        switch (command) {
            case SET_CAR_NUMBER -> {
                activeActon = SET_CAR_NUMBER;
                execute(SendMessage.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .text("?????????????? ?????????? ????????????")
                        .build())
                        .getMessageId();
            }
            case SET_DESCRIPTION -> {
                activeActon = SET_DESCRIPTION;
                execute(SendMessage.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .text("?????????????? ???????????????? ????????????")
                        .build())
                        .getMessageId();
            }
            case SET_IMAGE_PATH -> {
                activeActon = SET_IMAGE_PATH;
                execute(SendMessage.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .text("?????????????????? ?????????????????????? ????????????")
                        .build())
                        .getMessageId();
            }
            case CONFIRM_SAVE -> {
                if (eventHolder.getImagePath() != null) {
                    execute(SendPhoto.builder()
                            .chatId(callbackQuery.getMessage().getChatId())
                            .photo(new InputFile(new File(eventHolder.getImagePath())))
                            .caption("??????????: " + eventHolder.getTime() + "\n"
                                    + "?????????? ????????????: " + eventHolder.getCarNumber() + "\n"
                                    + "????????????????: " + eventHolder.getDescription())
                            .build());
                } else {
                    if (eventHolder.getCarNumber() != 0) {
                        execute(SendMessage.builder()
                                .chatId(callbackQuery.getMessage().getChatId())
                                .text("??????????: " + eventHolder.getTime() + "\n"
                                        + "????????????????: " + eventHolder.getDescription())
                                .build());
                    } else {
                        execute(SendMessage.builder()
                                .chatId(callbackQuery.getMessage().getChatId())
                                .text("??????????: " + eventHolder.getTime() + "\n"
                                        + "?????????? ????????????: " + eventHolder.getCarNumber() + "\n"
                                        + "????????????????: " + eventHolder.getDescription())
                                .build());
                    }
                }
                eventRepository.save(eventHolder);
                eventHolder = null;
                isCreatingNewEvent = false;
                createGeneralStaticKeyboard(callbackQuery.getMessage());
            }
        }
    }


    /**
     * ???????????????????? ?????????? eventHolder ?????????? ?????????????? ???????????? ???? ????????????????????
     */
    private void handleCommandsFromKeyboard(Message message) throws IOException, TelegramApiException {
        if (!activeActon.isEmpty()) {
            switch (activeActon) {
                case SET_CAR_NUMBER -> {
                    //???????????? ???????????? ????????????
                    eventHolder.setCarNumber(Integer.parseInt(message.getText()));
                    activeActon = "";
                    execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text("?????????? ???????????? ??????????????")
                            .build());
                }
                case SET_DESCRIPTION -> {
                    //???????????? ????????????????
                    eventHolder.setDescription(message.getText());
                    activeActon = "";
                    execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text("???????????????? ????????????????")
                            .build());
                }
                case SET_IMAGE_PATH -> {
                    //???????????? ????????????????????
                    if (Optional.ofNullable(message.getPhoto()).isPresent()) {
                        List<PhotoSize> list = message.getPhoto();
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh_mm_ss_dd_MM_yyyy");
                        eventHolder.setImagePath(uploadDocumentFromChat(
                                simpleDateFormat.format(date) + ".jpg", list.get(2).getFileId()));
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text("???????? ??????????????????")
                                .build());
                    } else {
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text("????????????. ?????????????????? ?????????????????? ????????.")
                                .build());
                    }
                }
            }
            activeActon = "";
            sendButtonsForCreatingEvent(message);
        }
    }

    /**
     * ???????????????? ???????????????????? ?????? ???????????????????? eventHolder
     */
    private void sendButtonsForCreatingEvent(Message message) throws TelegramApiException {
        if (eventHolder != null) {
            //?????????????? ?????????????? ????????????????????
            try {
                execute(DeleteMessage.builder().chatId(message.getChatId()).messageId(lastKeyboardId).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            List<InlineKeyboardButton> row1 = new ArrayList<>();
            if (eventHolder.getCarNumber() == 0) {
                row1.add(InlineKeyboardButton.builder()
                        .callbackData(SET_CAR_NUMBER)
                        .text("?????????? ????????????")
                        .build());
            }
            if (eventHolder.getDescription() == null) {
                row1.add(InlineKeyboardButton.builder()
                        .callbackData(SET_DESCRIPTION)
                        .text("????????????????")
                        .build());
            }
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            if (eventHolder.getImagePath() == null) {
                row2.add(InlineKeyboardButton.builder()
                        .callbackData(SET_IMAGE_PATH)
                        .text("????????")
                        .build());
            }
            List<InlineKeyboardButton> row3 = new ArrayList<>();
            if (eventHolder.getDescription() != null) {
                row3.add(InlineKeyboardButton.builder()
                        .callbackData(CONFIRM_SAVE)
                        .text("??????????????????")
                        .build());
            }
            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);
            keyboard.removeIf(Objects::isNull);
            inlineKeyboardMarkup.setKeyboard(keyboard);

            //???????????????? id ?????????????????????????? ?????????????????? ?? ??????????????????????
            lastKeyboardId = execute(SendMessage.builder()
                    .replyMarkup(inlineKeyboardMarkup)
                    .chatId(message.getChatId())
                    .text("???????? ???????????????? ??????????????")
                    .build()).getMessageId();
        }
    }


    /**
     * ???????????????? ???????????????????? ???? ?????????????????? ???? ????????????
     */
    private String uploadDocumentFromChat(String fileName, String fileId) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFileResponse = br.readLine();

        JSONObject jsonObject = new JSONObject(getFileResponse);
        JSONObject path = jsonObject.getJSONObject("result");
        String filePath = path.getString("file_path");

        File localFile = new File("uploadFiles/" + fileName);
        InputStream inputStream = new URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath).openStream();

        FileUtils.copyInputStreamToFile(inputStream, localFile);
        return localFile.getPath().replace("\\", "/");
    }

    private void createGeneralStaticKeyboard(Message message) throws TelegramApiException {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        if (isCreatingNewEvent) {
            keyboardFirstRow.add(new KeyboardButton("????????????"));

        } else {
            keyboardFirstRow.add(new KeyboardButton("?????????? ??????????????"));
        }

        keyboardRows.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        execute(SendMessage.builder()
                .text("??????????????? ???????????? ???????????????????????????????? ???????????????????? ????????????????.")
                .chatId(message.getChatId())
                .replyMarkup(replyKeyboardMarkup)
                .build()).getMessageId();
    }
}

