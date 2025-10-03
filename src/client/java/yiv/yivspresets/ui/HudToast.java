package yiv.yivspresets.ui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Lightweight, modern HUD toast: small, top-center, semi-transparent.
 */
public final class HudToast {
	private static volatile Text current;
	private static volatile long untilMs;
	private static boolean initialized = false;

	private HudToast() {}

	public static void init() {
		if (initialized) return;
		initialized = true;
	HudRenderCallback.EVENT.register((ctx, tickCounter) -> render(ctx));
	}

	public static void show(MinecraftClient mc, Text message) {
		if (mc == null) return;
		current = message;
		untilMs = System.currentTimeMillis() + 4500; // 4.5s
	}

	private static void render(DrawContext ctx) {
		if (current == null) return;
		long now = System.currentTimeMillis();
		if (now > untilMs) { current = null; return; }

		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer tr = mc.textRenderer;
		String s = current.getString();
		int textW = tr.getWidth(s);
		int padX = 8, padY = 5;
		int w = textW + padX * 2;
		int h = tr.fontHeight + padY * 2;

		int screenW = mc.getWindow().getScaledWidth();
		int x = Math.round(screenW / 2f - w / 2f);
		int y = 8; // top

		// Background: almost transparent black
		int bg = 0x99000000; // ARGB ~60% alpha
		ctx.fill(x, y, x + w, y + h, bg);

		// Text centered using Text API for wider version compatibility
		int textY = y + padY;
		try {
			ctx.drawCenteredTextWithShadow(tr, net.minecraft.text.Text.literal(s), screenW / 2, textY, 0xFFFFFFFF);
		} catch (Throwable ignored) {
			int tx = x + (w - textW) / 2;
			ctx.drawTextWithShadow(tr, s, tx, textY, 0xFFFFFFFF);
		}
	}
}
