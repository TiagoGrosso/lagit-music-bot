package org.tiagop.lagit.guild.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "guild_configs")
public class GuildConfig {

    @Id
    @Column(name = "guild_id", nullable = false)
    public String guildId;

    @Column(name = "inactivity_time_limit_seconds", nullable = false)
    public Long inactivityTimeLimitSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "announce_mode", nullable = false)
    public AnnounceMode announceMode;
}
