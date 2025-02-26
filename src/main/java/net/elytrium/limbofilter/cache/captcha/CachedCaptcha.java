/*
 * Copyright (C) 2021 Elytrium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.elytrium.limbofilter.cache.captcha;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.elytrium.limboapi.api.protocol.PreparedPacket;
import net.elytrium.limbofilter.LimboFilter;
import net.elytrium.limbofilter.Settings;

public class CachedCaptcha {

  private final List<CaptchaHandler> captchas = new ArrayList<>();
  private final AtomicInteger captchaCounter = new AtomicInteger(0);

  private final LimboFilter plugin;

  public CachedCaptcha(LimboFilter plugin) {
    this.plugin = plugin;
  }

  public void createCaptchaPacket(MinecraftPacket mapDataPacket, MinecraftPacket[] mapDataPackets17, String answer) {
    if (Settings.IMP.MAIN.CAPTCHA_GENERATOR.PREPARE_CAPTCHA_PACKETS) {
      PreparedPacket prepared = this.plugin.getFactory().createPreparedPacket();
      this.captchas.add(
          new CaptchaHandler(
              prepared.prepare(
                  mapDataPacket, ProtocolVersion.MINECRAFT_1_8
              ).prepare(
                  mapDataPackets17, ProtocolVersion.MINECRAFT_1_7_2, ProtocolVersion.MINECRAFT_1_7_6
              ), answer
          )
      );
    } else {
      this.captchas.add(new CaptchaHandler(mapDataPacket, mapDataPackets17, answer));
    }
  }

  public CaptchaHandler randomCaptcha() {
    int counter = this.captchaCounter.incrementAndGet();
    if (counter >= this.captchas.size()) {
      counter = 0;
      this.captchaCounter.set(0);
    }

    return this.captchas.get(counter);
  }
}
