package eu.nebulouscloud.securitymanager.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.InputStream;


@Deprecated
@ApplicationScoped
public class SshConnectionService {

    private static final Logger logger = Logger.getLogger(SshConnectionService.class);

    @ConfigProperty(name = "ssh.username")
    String username;

    @ConfigProperty(name = "ssh.key-path")
    String privateKeyPath;

    @ConfigProperty(name = "ssh.host")
    String host;

    @ConfigProperty(name = "ssh.port", defaultValue = "22")
    int port;

    private Session session;

    public void connect() {
        if (session != null && session.isConnected()) {
            // Already connected
            return;
        }
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKeyPath);

            session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            logger.infof("SSH Connection established.");
        } catch (Exception e) {
            logger.errorf(e, "SSH Connection failed.");
        }
    }
    public String executeCommand(String command) {
        StringBuilder outputBuffer = new StringBuilder();

        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();

            int readByte = inputStream.read();
            while (readByte != 0xffffffff) {
                outputBuffer.append((char) readByte);
                readByte = inputStream.read();
            }

            channel.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputBuffer.toString();
    }


    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            logger.infof("SSH Connection closed.");
        }
    }
}
