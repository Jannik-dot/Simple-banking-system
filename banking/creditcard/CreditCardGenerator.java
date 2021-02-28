package banking.creditcard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that can create valid credit cards which are able to pass the luhn algorithm
 */
public class CreditCardGenerator {
    Random random = new Random();

    public CreditCardGenerator() {

    }

    public CreditCard create() {
        CreditCard card = new CreditCard();
        String MII = "4";
        String BIN = "00000";
        String accountIdentifier = String.valueOf((random.nextInt(999999999 - 100000000) + 100000000));
        String checksum = createChecksum(MII + BIN + accountIdentifier);
        card.setCardNumber(MII + BIN + accountIdentifier + checksum);
         card.setPassword(String.valueOf(random.nextInt(9999 - 1000) + 1000));
        return card;
    }

    // luhn-algorithm
    public String createChecksum(String number) {
        List<Integer> list = Arrays.stream(number.split(""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 0) {
                list.set(i, list.get(i) * 2);
                if (list.get(i) > 9) {
                    list.set(i, list.get(i) - 9);
                }
            }
        }


        int total = list.stream().reduce(0, (sum, next) -> sum + next);

        for (int i = 0; i < 10; i++) {
            if ((i + total) % 10 == 0) {
                return String.valueOf(i);
            }
        }

        return "ERROR";
    }

}
