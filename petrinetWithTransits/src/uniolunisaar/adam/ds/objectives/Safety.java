package uniolunisaar.adam.ds.objectives;

import java.util.HashSet;
import java.util.Set;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public class Safety extends Condition {

    private final Set<Place> badPlaces;
    private boolean existential;

    public Safety() {
        this(true);
    }

    public Safety(boolean existential) {
        badPlaces = new HashSet<>();
        this.existential = existential;
    }

    public Safety(Safety safety) {
        this.existential = safety.existential;
        this.badPlaces = new HashSet<>(safety.badPlaces);
    }

    @Override
    public void buffer(PetriNetWithTransits net) {
        for (Place place : net.getPlaces()) {
            if (net.isBad(place)) {
                badPlaces.add(place);
            }
        }
        // java 1.8
//        game.getNet().getPlaces().stream().filter((place) -> (place.hasExtension("bad"))).forEach((place) -> {
//            badPlaces.add(place);
//        });
    }

    public Set<Place> getBadPlaces() {
        return badPlaces;
    }

    public void setExistential(boolean existential) {
        this.existential = existential;
    }

    @Override
    public Objective getObjective() {
        return existential ? Objective.E_SAFETY : Objective.A_SAFETY;
    }

    @Override
    public Safety getCopy() {
        return new Safety(this);
    }

}
