package org.tiagop.lagit.guild.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "guild_configs")
public class GuildConfig {

    @Id
    @Column(name = "guild_id", nullable = false)
    public String guildId;
}
