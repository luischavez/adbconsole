/*
 * Copyright (C) 2017 Frost
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.frostleviathan.adbconsole;

import java.io.File;
import java.util.List;

/**
 *
 * @author Frost
 */
public class Main {

    public static void main(String... args) {
        AdbConsole console = new AdbConsole("C:/Users/Frost/Downloads/apks/platform-tools/");

        console.setOutputStream(System.out);
        console.setErrorStream(System.err);

        List<Device> devices = console.devices();
        devices.forEach((device) -> {
            System.out.printf("[%s]%s = %b" + System.lineSeparator(),
                    device.model(), device.deviceId(), device.isAuthorized());
        });

        if (!devices.isEmpty()) {
            Device connectedDevice = devices.get(0);

            console.waitFor(connectedDevice, (device, connected) -> {
                System.out.printf("%s= %b" + System.lineSeparator(),
                        device.deviceId(), connected);
            });

            File apk = new File("C:/Users/Frost/Downloads/apks/com.billpocket.billpocket_3.4.8.apk");

            console.install(connectedDevice, apk, true, null);
        }
    }

}
