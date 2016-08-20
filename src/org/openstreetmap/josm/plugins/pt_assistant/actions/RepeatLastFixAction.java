package org.openstreetmap.josm.plugins.pt_assistant.actions;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.plugins.pt_assistant.PTAssistantPlugin;
import org.openstreetmap.josm.plugins.pt_assistant.validation.SegmentChecker;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Shortcut;

public class RepeatLastFixAction extends JosmAction {

    private static final long serialVersionUID = 2681464946469047054L;
    
    public RepeatLastFixAction() {
        super(tr("Repeat last fix"), new ImageProvider("presets/transport", "bus.svg"), tr("Repeat last fix"),
                Shortcut.registerShortcut("Repeat last fix", tr("Repeat last fix"), KeyEvent.VK_E, Shortcut.NONE),
                false, "repeatLastFix", false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        System.out.println("in actionPerformed");
        
        if (!isEnabled() || !Main.isDisplayingMapView()) {
            return;
        }
        
        System.out.println("performing action");
        
        SegmentChecker.carryOutRepeatLastFix(PTAssistantPlugin.getLastFix());
        
        PTAssistantPlugin.setLastFix(null);
        
    }

}
