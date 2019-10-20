package ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.connectike.util.Const;

public class HUD {
	
	public Stage stage;
	private Viewport viewport;
	
	Label steelPlatesLabel;
	Label copperWireLabel;
	Label electronicCircuitsLabel;

	public HUD(SpriteBatch sb) {
		
		viewport = new FitViewport(Const.V_WIDTH, Const.V_HEIGHT);
		
		stage = new Stage(viewport, sb);
		
		Table table = new Table();
		table.top();
		table.setFillParent(true);
		
		steelPlatesLabel = new Label("0", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		copperWireLabel = new Label("0", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		electronicCircuitsLabel = new Label("0", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		
		table.add(steelPlatesLabel).expandX();
		table.add(copperWireLabel).expandX();
		table.add(electronicCircuitsLabel).expandX();
		
		stage.addActor(table);
	}
	
	public void update(int n_steelPlates, int n_copperWire, int n_electronicCircuits) {
		steelPlatesLabel.setText("Steel Plates " + Integer.toString(n_steelPlates));
		copperWireLabel.setText("Copper Wire " + Integer.toString(n_copperWire));
		electronicCircuitsLabel.setText("Electronic Circuits " + Integer.toString(n_electronicCircuits));
	}
}
