package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides methods to handle functionalities in the IDS message handlers.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class MessageUtils {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);

    private Connector connector;

    @Autowired
    /**
     * <p>Constructor for MessageUtils.</p>
     *
     * @param connector a {@link de.fraunhofer.iais.eis.Connector} object.
     * @param configurationContainer a {@link de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer} object.
     */
    public MessageUtils(ConfigurationContainer configurationContainer) {
        this.connector = configurationContainer.getConnector();
    }

    /**
     * Extracts the uuid from a uri.
     *
     * @param uri The base uri.
     * @return Uuid as String.
     */
    public UUID uuidFromUri(URI uri) {
        Pattern pairRegex = Pattern.compile("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}");
        Matcher matcher = pairRegex.matcher(uri.toString());
        String uuid = "";
        while (matcher.find()) {
            uuid = matcher.group(0);
        }
        return UUID.fromString(uuid);
    }

    /**
     * Checks if the outbound model version of the requesting connector is listed in the inbound model versions.
     *
     * @param versionString The outbound model version of the requesting connector.
     * @return True on no hit, hence incompatibility.
     */
    public boolean checkForIncompatibleVersion(String versionString) {
        int counter = 0;
        for (String version : connector.getInboundModelVersion()) {
            if (version.equals(versionString)) {
                counter++;
            }
        }
        return counter == 0;
    }
}
