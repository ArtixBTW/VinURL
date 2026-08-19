package com.vinurl.client;

import static com.vinurl.util.Constants.MOD_ID;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.mojang.serialization.DataResult;
import com.vinurl.util.Url;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Config;
import net.blay09.mods.balm.platform.config.reflection.NestedType;
import net.blay09.mods.balm.platform.config.reflection.ValidateWith;
import net.blay09.mods.balm.platform.config.schema.ConfigValidator;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

@Config(value = MOD_ID, type = "client")
public class ClientConfig {
	public static void register() {
		Balm.config().registerConfig(ClientConfig.class);
	}

	public static ClientConfig get() {
		// i had all sorts of issues when this was stored in a singleton
		return Balm.config().getActiveConfig(ClientConfig.class);
	}

	public static void update(Consumer<ClientConfig> updater) {
		Balm.config().updateLocalConfig(ClientConfig.class, updater);
	}

	public General general = new General();

	public static class General {
		public boolean downloadEnabled = true;
		public boolean updatesOnStartup = true;
		public boolean showDescription = true;

		@NestedType(String.class)
		@ValidateWith(WhitelistValidator.class)
		public Set<String> urlWhitelist = new HashSet<>();

		public static class WhitelistValidator implements ConfigValidator<String> {
			@Override
			public DataResult<String> validate(String url) {
				boolean valid = Url.isValid(url);
				return valid
						? DataResult.success(url)
						: DataResult.error(() -> {
							Component component = url.isEmpty()
									? Component.translatable("vinurl.configuration.error.urlWhitelist.empty")
									: Component.translatable("vinurl.configuration.error.urlWhitelist.invalid", url);

							return component.getString();
						});
			}
		}
	}

	public Download download = new Download();

	public static class Download {
		public AudioQuality audioBitrate = AudioQuality.MEDIUM;

		public enum AudioQuality implements StringRepresentable {
			LOW("48K"),
			MEDIUM("96K"),
			HIGH("128K");

			private final String value;

			AudioQuality(String value) {
				this.value = value;
			}

			public String getValue() {
				return value;
			}

            @Override
            public String getSerializedName() {
				return name();
            }
		}

		public String parameters = "";
	}
}
