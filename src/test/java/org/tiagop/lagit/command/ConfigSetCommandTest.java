package org.tiagop.lagit.command;

import static org.assertj.core.api.Assertions.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

class ConfigSetCommandTest {

    @Test
    void it_registers_the_key_and_value_options() {
        // given / when
        final var commandData = (SlashCommandData) new ConfigSetCommand().toCommandData();

        // then
        assertThat(commandData.getOptions())
            .extracting(OptionData::getName)
            .containsExactly(ConfigOptions.CONFIG_KEY.getName(), ConfigSetCommand.CONFIG_VALUE_OPTION.getName());

        final var keyOption = commandData.getOptions().get(0);
        assertThat(keyOption.isRequired()).isTrue();
        assertThat(keyOption.isAutoComplete()).isTrue();

        final var valueOption = commandData.getOptions().get(1);
        assertThat(valueOption.isRequired()).isTrue();
        assertThat(valueOption.isAutoComplete()).isFalse();
    }
}
