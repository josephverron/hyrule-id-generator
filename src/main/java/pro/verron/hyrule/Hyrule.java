package pro.verron.hyrule;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Hyrule {

    static {
        readLoggingConfiguration("logging.properties");
    }

    @SneakyThrows
    private static void readLoggingConfiguration(String name) {
        LogManager logManager = LogManager.getLogManager();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
        logManager.readConfiguration(is);
    }

    @SneakyThrows
    public static void main(String[] args) {
        // TODO: Make all those parameters as program input (args or properties file)
        // TODO: Maybe could allow to start as a command line program as an option
        int nbDigitsInIdRepresentation = 9;
        String prngStartingSeed = "Hyrule";
        int listeningPort = 8888;
        int serverDyingTimeout = 10;

        Iterator<Id> idIterator = Hyrule
                .idGenerator(nbDigitsInIdRepresentation, prngStartingSeed)
                .iterator();

        HyruleServer hyruleServer = new HyruleServer(idIterator, listeningPort, serverDyingTimeout);
        hyruleServer.run();
    }

    public static Generator<Id> idGenerator(int nbChar, String initialSeed) throws NoSuchAlgorithmException {
        SecureRandom random = getSecureRandom(initialSeed);
        Iterator<Id> randomIdIterator = new RandomIdIterator(nbChar, random);
        Iterator<Id> distinctIdIterator = new Generator<>(randomIdIterator).stream().distinct().iterator();
        return new Generator<>(distinctIdIterator);
    }

    /**
     * Will create a SHA1PRNG random algorithm instance, and seed it with the given String bytes.
     * Do not hesitate to give a really long input String.
     * That algorithm has been chosen for its strength, and for the ability to be fully seeded, so allowing unit testing
     *
     * @param initialSeed will be used to seed the SecureRandom instance
     * @return a seeded SecureRandom instance
     * @throws NoSuchAlgorithmException in case there is no provider for SHA1PRNG algorithm
     */
    private static SecureRandom getSecureRandom(String initialSeed) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(initialSeed.getBytes(StandardCharsets.UTF_8));
        return random;
    }

}
