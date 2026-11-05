package org.tiagop.lagit.command;

import static org.assertj.core.api.Assertions.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

class ConfigGetCommandTest {

    @Test
    void it_registers_only_the_key_option() {
        // given / when
        final var commandData = (SlashCommandData) new ConfigGetCommand().toCommandData();

        // then
        assertThat(commandData.getOptions()).hasSize(1);

        final var keyOption = commandData.getOptions().getFirst();
        assertThat(keyOption.getName()).isEqualTo(ConfigOptions.CONFIG_KEY.getName());
        assertThat(keyOption.isRequired()).isTrue();
        assertThat(keyOption.isAutoComplete()).isTrue();
    }
}
