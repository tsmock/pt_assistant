package org.openstreetmap.josm.plugins.pt_assistant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;

public class PTStop extends RelationMember {

    private Node stopPosition = null;
    private OsmPrimitive platform = null;
    
    private String name = "";

    public PTStop(RelationMember other) throws IllegalArgumentException {

        super(other);

        if ((other.hasRole("stop") || other.hasRole("stop_entry_only") || other.hasRole("stop_exit_only"))&& other.getType().equals(OsmPrimitiveType.NODE)) {

            this.stopPosition = other.getNode();
            this.name = stopPosition.get("name");

        } else if (other.getRole().equals("platform") || other.getRole().equals("platform_entry_only")
                || other.getRole().equals("platform_exit_only")) {

            this.platform = other.getMember();
            this.name = platform.get("name");

        } else {
            throw new IllegalArgumentException("The RelationMember type does not match its role");
        }

    }

    /**
     * Adds the given element to the stop after a check
     * 
     * @param member
     *            Element to add
     * @return true if added successfully, false otherwise. A false value
     *         indicates either that the OsmPrimitiveType of the given
     *         RelationMember does not match its role or that this PTStop
     *         already has an attribute with that role.
     */
    public boolean addStopElement(RelationMember member) {

        // each element is only allowed once per stop

        // add stop position:
        if (member.hasRole("stop") || member.hasRole("stop_entry_only") || member.hasRole("stop_exit_only")) {
            if (member.getType().equals(OsmPrimitiveType.NODE) && stopPosition == null) {
                this.stopPosition = member.getNode();
                return true;
            }
        }

        // add platform:
        if (member.getRole().equals("platform") || member.getRole().equals("platform_entry_only")
                || member.getRole().equals("platform_exit_only")) {
            if (platform == null) {
                platform = member.getMember();
                return true;
            }
        }

        return false;

    }

    /**
     * Returns the stop_position for this PTstop. If the stop_position is not
     * available directly, the method searches for a stop_area relation
     * 
     * @return
     */
    public Node getStopPosition() {

        // List<OsmPrimitive> referrers = platform.getReferrers();
        // List<Relation> stopAreaRelations = new ArrayList<>();
        // for (OsmPrimitive referrer: referrers) {
        // if (referrer.getType().equals(OsmPrimitiveType.RELATION) &&
        // referrer.hasTag("public_tranport", "stop_area")) {
        // stopAreaRelations.add((Relation)referrer);
        // }
        // }
        //
        // for (Relation stopArea: stopAreaRelations) {
        // for (RelationMember rm: stopArea.getMembers()) {
        // if (rm.hasRole("stop") && rm.getType().equals(OsmPrimitiveType.NODE))
        // {
        // this.stopPosition = rm.getNode();
        // }
        // }
        // }

        return this.stopPosition;
    }

    /**
     * Returns platform (including platform_entry_only and platform_exit_only)
     * 
     * @return
     */
    public OsmPrimitive getPlatform() {
        return this.platform;
    }
    
    public String getName() {
        return this.name;
    }

    public void setStopPosition(Node newStopPosition) {

        this.stopPosition = newStopPosition;

    }

    /**
     * Finds potential stop_positions of the platform of this PTStop. It only
     * makes sense to call this method if the stop_position attribute is null.
     * The stop_positions are potential because they may refer to a different
     * route, which this method does not check.
     * 
     * @return List of potential stop_positions for this PTStop
     */
    public List<Node> findPotentialStopPositions() {

        ArrayList<Node> potentialStopPositions = new ArrayList<>();

        if (platform == null) {
            return potentialStopPositions;
        }

        // 1) Look for any stop_area relations that this platform
        // belongs to:
        ArrayList<OsmPrimitive> platformList = new ArrayList<OsmPrimitive>(1);
        platformList.add(platform);
        Set<Relation> platformParentRelations = OsmPrimitive.getParentRelations(platformList);
        ArrayList<Relation> stopAreaList = new ArrayList<Relation>();
        for (Relation platformParentRelation : platformParentRelations) {
            if (platformParentRelation.hasTag("public_transport", "stop_area")) {
                stopAreaList.add(platformParentRelation);
            }
        }

        // 2) Get all potential stop_positions from those stop_area relations:
        for (Relation stopArea : stopAreaList) {
            for (RelationMember rm : stopArea.getMembers()) {
                if ((rm.hasRole("stop") || rm.hasRole("stop_entry_only") || rm.hasRole("stop_exit_only"))&& rm.getType().equals(OsmPrimitiveType.NODE)
                        && rm.getNode().hasTag("public_transport", "stop_position")) {
                    potentialStopPositions.add(rm.getNode());
                }
            }
        }

        return potentialStopPositions;
    }

}
