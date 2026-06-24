package com.alexfh.external_text_edit;

import com.alexfh.external_text_edit.config.ExternalTextEditConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ExternalTextEdit implements ClientModInitializer
{
    public static final String modID = "external-text-edit";
    public static final Logger LOGGER = LoggerFactory.getLogger(modID);
    public static boolean processingChatScreenKeyPress = false;
    private static ConfigHolder<ExternalTextEditConfig> configHolder;

    public static ExternalTextEditConfig getConfig()
    {
        return ExternalTextEdit.configHolder.getConfig();
    }

    private static String processArgument(String argument, String tempFilePath, int cursorLine, int cursorCol)
    {
        return argument.replace("{file}", tempFilePath).replace("{line0}", Integer.toString(cursorLine))
            .replace("{line}", Integer.toString(cursorLine + 1)).replace("{column0}", Integer.toString(cursorCol))
            .replace("{column}", Integer.toString(cursorCol + 1));
    }

    private static String[] getCommand(String tempFilePath, int cursorLine, int cursorCol)
    {
        return ExternalTextEdit.getConfig().editorCommand.stream()
            .map(argument -> processArgument(argument, tempFilePath, cursorLine, cursorCol)).toArray(String[]::new);
    }

    public static void editFieldWithExternalEditor(EditBox textFieldWidget)
    {
        List<String> toEditLines = new ArrayList<>();
        int cursorCol = textFieldWidget.getCursorPosition();
        int cursorLine = 0;
        if (ExternalTextEdit.processingChatScreenKeyPress)
        {
            Minecraft minecraftClient = Minecraft.getInstance();
            toEditLines.addAll(minecraftClient.gui.hud.getChat().getRecentChat());
            cursorLine += toEditLines.size();
        }
        toEditLines.add(textFieldWidget.getValue());
        ExternalTextEdit.editFieldWithExternalEditor(textFieldWidget, toEditLines, cursorCol, cursorLine);
    }

    public static void editFieldWithExternalEditor(EditBox textFieldWidget, List<String> toEditLines,
                                                   int cursorCol, int cursorLine)
    {
        WeakReference<EditBox> textFieldWidgetReference = new WeakReference<>(textFieldWidget);
        Minecraft minecraftClient = Minecraft.getInstance();
        CompletableFuture.supplyAsync(() ->
        {
            try
            {
                File tempFile = File.createTempFile("ete-editor", null);
                tempFile.deleteOnExit();
                Path tempFilePath = tempFile.toPath();
                Files.write(tempFilePath, toEditLines);
                String[] command = ExternalTextEdit.getCommand(tempFile.getAbsolutePath(), cursorLine, cursorCol);
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                process.waitFor();
                return Files.readAllLines(tempFilePath);
            }
            catch (IOException | InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync((editedLines, error) ->
        {
            if (error != null)
            {
                String message = error.getMessage();
                if (message == null)
                {
                    message = "Unknown failure";
                }
                MutableComponent errorText = MutableComponent.create(PlainTextContents.create("External editing failed: " + message))
                    .withStyle(style -> style.withColor(ChatFormatting.RED));
                minecraftClient.gui.hud.getChat().addClientSystemMessage(errorText);
                return;
            }
            EditBox originalTextFieldWidget = textFieldWidgetReference.get();
            if (originalTextFieldWidget == null)
            {
                return;
            }
            if (!editedLines.isEmpty())
            {
                originalTextFieldWidget.setValue(editedLines.get(editedLines.size() - 1));
            }
        }, minecraftClient);
    }

    @Override
    public void onInitializeClient()
    {
        AutoConfig.register(ExternalTextEditConfig.class, GsonConfigSerializer::new);
        configHolder = AutoConfig.getConfigHolder(ExternalTextEditConfig.class);
    }
}