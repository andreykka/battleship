package com.battleship.generator;

import com.battleship.components.Field;
import com.battleship.components.Ship;
import com.battleship.enumeration.Direction;
import com.battleship.enumeration.ShipType;
import org.apache.commons.lang3.tuple.Pair;

import java.security.SecureRandom;
import java.util.Random;

import static com.battleship.enumeration.Direction.HORIZONTAL;
import static com.battleship.enumeration.Direction.values;
import static com.battleship.enumeration.ShipType.SINGLE_DECKER;

public class ShipGenerator {

    private static final Random RANDOM = new SecureRandom();
    private final int iterationsBeforeException;

    private Field field;

    /**
     * Construct instance of ShipGenerator that produces ships.
     *
     * @param field field to check either ship can be placed on it.
     */
    public ShipGenerator(Field field) {
        this.field = field;
        int squareOfField = field.getFieldSize() * field.getFieldSize();
        this.iterationsBeforeException = squareOfField * 10;
    }

    /**
     * Create new ship of desired type that can be places(without overlapping) on existing field.
     *
     * @param shipType type of ship to produce.
     * @return new ship
     */
    public Ship generate(ShipType shipType) {
        // number of iteration to generate suitable ship
        int sanityIdentifier = iterationsBeforeException;

        while (sanityIdentifier-- > 0) {
            Ship ship = generateNewShip(field.getFieldSize(), shipType);
            if (field.canBePlaced(ship)) {
                return ship;
            }
        }

        throw new RuntimeException("not enough space to put new ship of type: " + shipType);

    }

    private Ship generateNewShip(int fieldSize, ShipType shipType) {
        Direction direction = getArbitraryDirection();

        if (shipType == SINGLE_DECKER) {
            int positionX = RANDOM.nextInt(fieldSize);
            int positionY = RANDOM.nextInt(fieldSize);
            return new Ship(positionX, positionY, shipType, direction);
        }

        Pair<Integer, Integer> lastCoordinatesPair = getPairOfLatestValidCoordinates(fieldSize, shipType, direction);

        int maxX = lastCoordinatesPair.getLeft();
        int maxY = lastCoordinatesPair.getRight();

        int positionX = RANDOM.nextInt(maxX);
        int positionY = RANDOM.nextInt(maxY);
        return new Ship(positionX, positionY, shipType, direction);

    }

    private Pair<Integer, Integer> getPairOfLatestValidCoordinates(int fieldSize, ShipType shipType, Direction direction) {
        int shipLength = shipType.getDeckerCount();
        // because coordinates starts from 0
        int limitPosition = fieldSize - 1;
        int limitPositionIfShipLaysOnTheLine = fieldSize - shipLength;

        return direction == HORIZONTAL
                ? Pair.of(limitPositionIfShipLaysOnTheLine, limitPosition)
                : Pair.of(limitPosition, limitPositionIfShipLaysOnTheLine);
    }

    private static Direction getArbitraryDirection() {
        Direction[] directions = values();
        return directions[RANDOM.nextInt(100) % directions.length];
    }

}
