package yiv.yivspresets.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/** Simple, centered Yes/No confirmation dialog. */
public class ConfirmDialogScreen extends Screen {
    private final Screen parent;
    private final Text message;
    private final Consumer<Boolean> callback;

    public ConfirmDialogScreen(Screen parent, Text message, Consumer<Boolean> callback) {
        super(Text.literal("Confirm"));
        this.parent = parent;
        this.message = message;
        this.callback = callback;
    }

    @Override
    protected void init() {
        int y = this.height / 3;

        int btnW = 80;
        int gap = 12;
        int rowW = btnW * 2 + gap;
        int left = Math.round(this.width / 2f - rowW / 2f);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Yes"), b -> {
            if (callback != null) callback.accept(true);
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(left, y + 30, btnW, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("No"), b -> {
            if (callback != null) callback.accept(false);
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(left + btnW + gap, y + 30, btnW, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(ctx);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 3 - 20, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, message, this.width / 2, this.height / 3, 0xFFFFFF);
    }
}
