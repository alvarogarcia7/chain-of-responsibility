package pl.refactoring.chain;

import pl.refactoring.chain.card.Card;
import pl.refactoring.chain.card.RANK;
import pl.refactoring.chain.card.SUIT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static pl.refactoring.chain.RANKING.*;

/**
 * Corypight (c) 2018 IT Train Wlodzimierz Krakowski (www.refactoring.pl)
 * <p>
 * This code is exclusive property of Wlodek Krakowski
 * for usage of attendees of trainings that are conducted by Wlodek Krakowski.
 * <p>
 * This code may not be copied or used without
 * written consent of IT Train Wlodzimierz Krakowski (www.refactoring.pl)
 * <p>
 * If willing to do so, please contact the author.
 */
public class HandResolver {
    public Hand hand(CardSet cardSet) {
        List<Card> handCards = cardSet.getSortedCards();

        // Figure our high card by same color
        SUIT colorCandidate = handCards.get(0).getSuit();
        boolean allSameColor = getStream(handCards)
                .allMatch(card -> card.getSuit().equals(colorCandidate));
        if (isStraightFlush(handCards)) {
            return new Hand(STRAIGHT_FLUSH, handCards);
        }
        if (isFlush(handCards)) {
            return new Hand(FLUSH, handCards);
        }

        //TODO AGB mirar que plugin es para los shortcuts! academy
        if (!allSameColor) {
            Map<RANK, List<Card>> cardsByRank = handCards.stream().collect(groupingBy(Card::getRank));

            List<RANK> ranks = new ArrayList<>(cardsByRank.keySet());
            if (ranks.size() == 5) {
                // Check for straight
                Ordinals ordinals = new Ordinals(handCards).invoke();
                int firstOrdinal = ordinals.getFirstOrdinal();
                int secondOrdinal = ordinals.getSecondOrdinal();
                int thirdOrdinal = ordinals.getThirdOrdinal();
                int fourthOrdinal = ordinals.getFourthOrdinal();
                int fifthOrdinal = ordinals.getFifthOrdinal();

                if (firstOrdinal + 1 == secondOrdinal
                        && secondOrdinal + 1 == thirdOrdinal
                        && thirdOrdinal + 1 == fourthOrdinal
                        && fourthOrdinal + 1 == fifthOrdinal)
                    return new Hand(STRAIGHT, handCards);
            }
            if (ranks.size() == 2) {
                // Look for four of a kind
                if (cardsByRank.get(ranks.get(0)).size() == 4 ||
                        cardsByRank.get(ranks.get(1)).size() == 4)
                    return new Hand(FOUR_OF_A_KIND, handCards);
                    // Look for full house
                else {
                    return new Hand(FULL_HOUSE, handCards);
                }
            } else if (ranks.size() == 3) {
                // Look for 3 of a kind
                if (cardsByRank.get(ranks.get(0)).size() == 3 ||
                        cardsByRank.get(ranks.get(1)).size() == 3 ||
                        cardsByRank.get(ranks.get(2)).size() == 3)
                    return new Hand(THREE_OF_A_KIND, handCards);

                // Look for 2 pairs
                if (cardsByRank.get(ranks.get(0)).size() == 1 ||
                        cardsByRank.get(ranks.get(1)).size() == 1 ||
                        cardsByRank.get(ranks.get(2)).size() == 1)
                    return new Hand(TWO_PAIRS, handCards);
            } else if (ranks.size() == 4) {
                return new Hand(ONE_PAIR, handCards);
            } else {
                return new Hand(HIGH_CARD, handCards);
            }
        }

        return new Hand(HIGH_CARD, handCards);
    }

    private boolean isFlush(List<Card> handCards) {
        boolean allSameColor = getStream(handCards).allMatch(card -> card.getSuit().equals(handCards.get(0).getSuit()));
        return allSameColor && !isStraightFlush(handCards);
    }

    private Stream<Card> getStream(List<Card> handCards) {
        return handCards.stream();
    }

    private boolean isStraightFlush(List<Card> handCards) {
        // Check for straight flush
        Ordinals ordinals = new Ordinals(handCards).invoke();
        int firstOrdinal = ordinals.getFirstOrdinal();
        int secondOrdinal = ordinals.getSecondOrdinal();
        int thirdOrdinal = ordinals.getThirdOrdinal();
        int fourthOrdinal = ordinals.getFourthOrdinal();
        int fifthOrdinal = ordinals.getFifthOrdinal();

        boolean allSameColor = getStream(handCards)
                .allMatch(card -> card.getSuit().equals(handCards.get(0).getSuit()));

        return allSameColor && firstOrdinal + 1 == secondOrdinal
                && secondOrdinal + 1 == thirdOrdinal
                && thirdOrdinal + 1 == fourthOrdinal
                && fourthOrdinal + 1 == fifthOrdinal;
    }


    private class Ordinals {
        private List<Card> handCards;
        private int firstOrdinal;
        private int secondOrdinal;
        private int thirdOrdinal;
        private int fourthOrdinal;
        private int fifthOrdinal;

        public Ordinals(List<Card> handCards) {
            this.handCards = handCards;
        }

        public int getFirstOrdinal() {
            return firstOrdinal;
        }

        public int getSecondOrdinal() {
            return secondOrdinal;
        }

        public int getThirdOrdinal() {
            return thirdOrdinal;
        }

        public int getFourthOrdinal() {
            return fourthOrdinal;
        }

        public int getFifthOrdinal() {
            return fifthOrdinal;
        }

        public Ordinals invoke() {
            firstOrdinal = handCards.get(0).getRank().ordinal();
            secondOrdinal = handCards.get(1).getRank().ordinal();
            thirdOrdinal = handCards.get(2).getRank().ordinal();
            fourthOrdinal = handCards.get(3).getRank().ordinal();
            fifthOrdinal = handCards.get(4).getRank().ordinal();
            return this;
        }
    }
}
