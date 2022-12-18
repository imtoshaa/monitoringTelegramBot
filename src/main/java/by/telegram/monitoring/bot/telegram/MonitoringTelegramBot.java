package by.telegram.monitoring.bot.telegram;

import by.telegram.monitoring.bot.entities.Event;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@AllArgsConstructor
@RequiredArgsConstructor
public class MonitoringTelegramBot extends TelegramLongPollingBot {

    private final String botUsername = "@Mikashevichi_bot";
    private final String botToken = "5636190624:AAFQHb5EemguvCOy6bbKF3JcCkhdfLcslkI";

    private Event eventHolder;
    private int lastKeyboardId = 0;
    private String activeActon = "";
    private boolean isCreatingNewEvent;

    private final String SET_CAR_NUMBER = "setCarNumber";
    private final String SET_DESCRIPTION = "setDescription";
    private final String SET_IMAGE_PATH = "setImagePath";
    private final String CONFIRM_SAVE = "confirmSave";


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
                handleStringsFromStaticKeyboard(update.getMessage());
                handleCommands(update.getMessage());
                handleCommandsFromKeyboard(update.getMessage());

                createGeneralStaticKeyboard(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Обработка комманд типа "/command"
     */
    private void handleCommands(Message message) throws TelegramApiException {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntities =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
//            String messageParameter;
//            try {
//                messageParameter = message.getText().split(" ")[1];
//            } catch (Exception e) {
//                messageParameter = "";
//            }
            if (commandEntities.isPresent()) {
                String command = message.getText()
                        .substring(commandEntities.get().getOffset(), commandEntities.get().getLength());
                switch (command) {
                    case "/start" -> {
                        execute(SendMessage.builder()
                                .text("""
                                        Вас приветствует телеграм-бот.
                                        Для взаимодействия с ботом Вы можете использовать следующие команды:
                                        /connect - Привязать бот к аккаунту
                                        /help - Помощь
                                        """)
                                .chatId(message.getChatId().toString())
                                .build());
                    }
                }
            }
        }
    }

    private void handleStringsFromStaticKeyboard(Message message) throws TelegramApiException {
        if (message.hasText()) {
            switch (message.getText()) {
                case "Новое событие" -> {
                    eventHolder = new Event();
                    isCreatingNewEvent = true;
                    Date date = new Date();
                    SimpleDateFormat formatDate = new SimpleDateFormat("H:mm:ss");
                    eventHolder.setTime(formatDate.format(date));
                    sendButtonsForCreatingEvent(message);
                }
                case "Отмена" -> {
                    isCreatingNewEvent = false;
                }
            }

        }
    }

    /**
     * Обработка callback с клавиатуры
     */
    private void handleCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        String command = callbackQuery.getData();

        switch (command) {
            case SET_CAR_NUMBER -> {
                activeActon = SET_CAR_NUMBER;
                execute(SendMessage.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .text("Введите номер машины")
                        .build())
                        .getMessageId();
            }
            case SET_DESCRIPTION -> {
                activeActon = SET_DESCRIPTION;
                execute(SendMessage.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .text("Введите описание ошибки")
                        .build())
                        .getMessageId();
            }
            case SET_IMAGE_PATH -> {
                activeActon = SET_IMAGE_PATH;
                execute(SendMessage.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .text("Загрузите изображение ошибки")
                        .build())
                        .getMessageId();
            }
            case CONFIRM_SAVE -> {
//                execute(SendMessage.builder()
//                        .chatId(callbackQuery.getMessage().getChatId())
//                        .text("Событие сохранено!" + "\n"
//                                + eventHolder.getTime() + "\n"
//                                + eventHolder.getCarNumber() + "\n"
//                        + eventHolder.getDescription() + "\n"
//                        + eventHolder.getImagePath())
//                        .build());
                execute(SendPhoto.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .photo(new InputFile(new File(eventHolder.getImagePath())))
                        .caption(eventHolder.getTime() + "\n"
                                + eventHolder.getCarNumber() + "\n"
                                + eventHolder.getDescription())
                        .build());
                eventHolder = null;
                isCreatingNewEvent = false;
            }
        }
    }

    /**
     * Заполнение полей eventHolder после нажатия кнопок на клавиатуре
     */
    private void handleCommandsFromKeyboard(Message message) throws IOException, TelegramApiException {
        if (!activeActon.isEmpty()) {
            switch (activeActon) {
                case SET_CAR_NUMBER -> {
                    //запись номера машины

                    eventHolder.setCarNumber(Integer.parseInt(message.getText()));
                    activeActon = "";
                    execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text("Номер машины записан")
                            .build());
                }
                case SET_DESCRIPTION -> {
                    //запись описания
                    eventHolder.setDescription(message.getText());
                    activeActon = "";
                    execute(SendMessage.builder()
                            .chatId(message.getChatId())
                            .text("Описание записано")
                            .build());
                }
                case SET_IMAGE_PATH -> {
                    //запись фотографии
                    if (Optional.ofNullable(message.getPhoto()).isPresent()) {
                        List<PhotoSize> list = message.getPhoto();
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh_mm_ss_dd_MM_yyyy");
                        eventHolder.setImagePath(uploadDocumentFromChat(
                                simpleDateFormat.format(date) + ".jpg", list.get(2).getFileId()));
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text("Фото сохранено")
                                .build());
                    } else {
                        execute(SendMessage.builder()
                                .chatId(message.getChatId())
                                .text("Ошибка. Требуется загрузить фото.")
                                .build());
                        return;
                    }
                }
            }
            sendButtonsForCreatingEvent(message);
        }
    }

    /**
     * Создание клавиатуры для заполнения eventHolder
     */
    private void sendButtonsForCreatingEvent(Message message) throws TelegramApiException {
        try {
            execute(DeleteMessage.builder().chatId(message.getChatId()).messageId(lastKeyboardId).build());
        } catch (Exception e) {
            e.getMessage();
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        if (eventHolder.getCarNumber() == 0) {
            row1.add(InlineKeyboardButton.builder()
                    .callbackData(SET_CAR_NUMBER)
                    .text("Номер машины")
                    .build());
        }
        if (eventHolder.getDescription() == null) {
            row1.add(InlineKeyboardButton.builder()
                    .callbackData(SET_DESCRIPTION)
                    .text("Описание")
                    .build());
        }
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        if (eventHolder.getImagePath() == null) {
            row2.add(InlineKeyboardButton.builder()
                    .callbackData(SET_IMAGE_PATH)
                    .text("Фото")
                    .build());
        }
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        if (eventHolder.getDescription() != null) {
            row3.add(InlineKeyboardButton.builder()
                    .callbackData(CONFIRM_SAVE)
                    .text("Сохранить")
                    .build());
        }
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.removeIf(Objects::isNull);
        inlineKeyboardMarkup.setKeyboard(keyboard);

        //получаем id отправленного сообщения с клавиатурой
        lastKeyboardId = execute(SendMessage.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(message.getChatId())
                .text("Выберите кнопку")
                .build()).getMessageId();
    }


    /**
     * Загрузка фотографий из телеграма на сервер
     */
    private String uploadDocumentFromChat(String fileName, String fileId) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFileResponse = br.readLine();

        JSONObject jsonObject = new JSONObject(getFileResponse);
        JSONObject path = jsonObject.getJSONObject("result");
        String filePath = path.getString("file_path");

        File localFile = new File("src/main/resources/uploadedFiles/" + fileName);
        InputStream inputStream = new URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath).openStream();

        FileUtils.copyInputStreamToFile(inputStream, localFile);

        return localFile.getPath();
    }

    private void createGeneralStaticKeyboard(Message message) throws TelegramApiException {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        if (isCreatingNewEvent) {
            keyboardFirstRow.add(new KeyboardButton("Отмена"));

        } else {
            keyboardFirstRow.add(new KeyboardButton("Новое событие"));
        }

        keyboardRows.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        execute(SendPoll.builder()
//                .text("Выберите действие")
                .chatId(message.getChatId())
                .replyMarkup(replyKeyboardMarkup)
                .build());
    }

    public void sendMessageToChat(long chatId, String message) throws TelegramApiException {
        execute(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .build());
    }


}

