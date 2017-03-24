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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementacion base de la consola de adb.
 *
 * @author Frost
 */
public class AdbConsole implements Console {

    /**
     * <b>Listeners</b> de la consola.
     */
    private final List<ConsoleListener> listeners = new ArrayList<>();

    /**
     * Directorio dodne estan ubicados los binarios de adb.
     */
    private File adbPath;

    /**
     * Canal donde se escribira la salida de los comandos.
     */
    private OutputStream outputStream;

    /**
     * Canal donde se escribiran los errores de los comandos.
     */
    private OutputStream errorStream;

    /**
     * Dispositivos conectados.
     */
    private final List<Device> devices = new CopyOnWriteArrayList<>();

    /**
     * Hilos en espera.
     */
    private final Map<String, WaitForThread> waiting
            = new ConcurrentHashMap<>();

    /**
     * Hilos de instalacion.
     */
    private final Map<String, InstallThread> installing
            = new ConcurrentHashMap<>();

    /**
     * Construye la consola a partir de una cadena con la ubicacion de los
     * binarios de adb.
     *
     * @param adbPath ubicacion de los binarios de adb
     * @throws AdbException
     */
    public AdbConsole(String adbPath) throws AdbException {
        File path = new File(adbPath);
        if (!path.exists()) {
            throw new AdbException(String.format("%s path not found", adbPath));
        }

        File[] files = path.listFiles();

        boolean found = false;
        for (File file : files) {
            String fileName = file.getName();
            if ("adb".equals(fileName) || "adb.exe".equals(fileName)) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new AdbException("adb binary not found");
        }

        this.adbPath = path;
    }

    /**
     * Manda la entradas desde <b>in</b> a la salida <b>out</b>.
     *
     * @param in entrada
     * @param out salida o null
     * @return entrada generada
     * @throws IOException
     */
    protected String writeTo(InputStream in, OutputStream out)
            throws IOException {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(in))) {

            BufferedWriter writer = null;
            if (null != out) {
                writer = new BufferedWriter(new OutputStreamWriter(out));

            }

            String line;
            while (null != (line = reader.readLine())) {
                builder.append(line).append(System.lineSeparator());

                if (null != writer) {
                    writer.append(line);
                    writer.newLine();
                }
            }

            if (null != writer) {
                writer.flush();
            }
        }

        return builder.toString();
    }

    /**
     * Crea un proceso a partir del comando especificado.
     *
     * @param command comando
     * @return proceso creado
     * @throws AdbException
     */
    protected Process createProcess(String command) throws AdbException {
        Runtime runtime = Runtime.getRuntime();

        try {
            String fullCommand = adbPath + File.separator + command;
            return runtime.exec(fullCommand, new String[0], adbPath);
        } catch (IOException ex) {
            throw new AdbException(
                    String.format("error executing the %s command", command),
                    ex);
        }
    }

    /**
     * Obtiene un listado de dispositivos conectados.
     *
     * @return dispositivos conectados
     */
    protected List<Device> readDevices() {
        AdbResult result = execute("adb devices -l");

        String output = result.output;
        output = output.replace("List of devices attached", "").trim();

        Pattern devicePattern = Pattern.compile("([a-zA-Z0-9]+)[\\s]+(.+)");
        Matcher deviceMatcher = devicePattern.matcher(output);

        Pattern statusPattern = Pattern.compile("model:(\\w+)");

        List<Device> connectedDevices = new ArrayList<>();

        while (deviceMatcher.find()) {
            String deviceId = deviceMatcher.group(1);

            if ("*".equals(deviceId)) {
                continue;
            }

            String status = deviceMatcher.group(2);
            String model = "unknow";
            boolean authorized = !"unauthorized".equals(status);

            Matcher statusMatcher = statusPattern.matcher(status);
            if (statusMatcher.find()) {
                model = statusMatcher.group(1);
            }

            AdbDevice device = new AdbDevice(deviceId, model, authorized);
            connectedDevices.add(device);

            listeners.forEach((listener) -> {
                listener.onDevice(device, true);
            });
        }

        Set<Map.Entry<String, WaitForThread>> entrySet = waiting.entrySet();
        Iterator<Map.Entry<String, WaitForThread>> iterator
                = entrySet.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, WaitForThread> entry = iterator.next();
            String deviceId = entry.getKey();
            WaitForThread thread = entry.getValue();

            if (!thread.isAlive()) {
                waiting.remove(deviceId);
            }
        }

        return connectedDevices;
    }

    /**
     * Establece el canal donde se escribira la salida de los comandos.
     *
     * @param outputStream canal
     */
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Establece el canal donde se escribiran los errores de los comandos.
     *
     * @param errorStream canal
     */
    public void setErrorStream(OutputStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public void addListener(ConsoleListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ConsoleListener listener) {
        listeners.remove(listener);
    }

    @Override
    public AdbResult execute(String command) throws AdbException {
        Process process = createProcess(command);

        StringBuilder builder = new StringBuilder();
        try {
            String output
                    = writeTo(process.getInputStream(), outputStream);
            builder.append(output).append(System.lineSeparator());

            String err
                    = writeTo(process.getErrorStream(), errorStream);
            builder.append(err).append(System.lineSeparator());
        } catch (IOException ex) {
            throw new AdbException("Error writing output", ex);
        }

        int exitValue = process.exitValue();
        process.destroy();

        return new AdbResult(0 == exitValue, builder.toString());
    }

    @Override
    public List<Device> devices() throws AdbException {
        List<Device> connectedDevices = readDevices();

        devices.clear();

        connectedDevices.forEach((device) -> {
            devices.add(device);
        });

        return devices;
    }

    @Override
    public Device update(Device device) throws AdbException {
        List<Device> connectedDevices = readDevices();

        Optional<Device> optionalDevice = connectedDevices.stream()
                .filter((d) -> d.deviceId().equals(device.deviceId()))
                .findFirst();

        devices.remove(device);

        if (optionalDevice.isPresent()) {
            devices.add(optionalDevice.get());
        }

        listeners.forEach((listener) -> {
            listener.onDevice(device, optionalDevice.isPresent());
        });

        return optionalDevice.orElse(null);
    }

    @Override
    public void waitFor(Device device, OnDeviceCallback callback)
            throws AdbException {
        if (waiting.containsKey(device.deviceId())) {
            return;
        }

        String command
                = String.format("adb -s %s wait-for-usb-device",
                        device.deviceId());
        Process process = createProcess(command);

        WaitForThread thread
                = new WaitForThread(this, device, process, callback);

        waiting.put(device.deviceId(), thread);

        thread.start();
    }

    @Override
    public void stopWait(Device device) throws AdbException {
        WaitForThread thread = waiting.get(device.deviceId());

        if (null != thread) {
            thread.stopWait();
        }

        waiting.remove(device.deviceId());
    }

    @Override
    public boolean isWaiting(Device device) throws AdbException {
        return waiting.containsKey(device.deviceId());
    }

    @Override
    public boolean isAuthorized(Device device) throws AdbException {
        Device updatedDevice = update(device);

        return null != updatedDevice && updatedDevice.isAuthorized();
    }

    @Override
    public void install(Device device, File[] files, boolean force,
            OnInstallCallback callback)
            throws AdbException, UnauthorizedDeviceException {
        Device updatedDevice = update(device);

        if (!updatedDevice.isAuthorized()) {
            throw new UnauthorizedDeviceException(
                    String.format("unauthorized device %s", device.deviceId()));
        }

        if (0 == files.length) {
            throw new AdbException("select files to install");
        }

        for (File file : files) {
            if (!file.exists()) {
                throw new AdbException(
                        String.format("%s not exists", file.getAbsolutePath()));
            }
        }

        String baseCommand = String.format("adb -s %s install %s",
                device.deviceId(), force ? "-r" : "");

        InstallThread thread
                = new InstallThread(this, device, baseCommand, files, callback);

        installing.put(device.deviceId(), thread);

        thread.start();

    }

    @Override
    public void cancel(Device device) throws AdbException {
        InstallThread thread = installing.get(device.deviceId());

        if (null != thread) {
            thread.stopInstall();
        }

        installing.remove(device.deviceId());
    }

    @Override
    public boolean isInstalling(Device device) throws AdbException {
        return installing.containsKey(device.deviceId());
    }

    @Override
    public void init() throws AdbException {
        execute("adb start-server");
    }

    @Override
    public void kill() throws AdbException {
        execute("adb kill-server");
    }

}
