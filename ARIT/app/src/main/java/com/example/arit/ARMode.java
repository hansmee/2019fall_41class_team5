package com.example.arit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;



public class ARMode extends AppCompatActivity {
    private ArFragment fragment;
    private Button back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armode);

        Intent intent = getIntent();
        double length = intent.getExtras().getDouble("length");
        double height = intent.getExtras().getDouble("height");
        double width = intent.getExtras().getDouble("width");



        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            createCube(hitResult.createAnchor(), length, height, width);

        });
    }




    private void createCube(Anchor anchor, double length, double height, double width) {

        MaterialFactory
                .makeOpaqueWithColor(this, new Color(android.graphics.Color.rgb(200, 200, 200)))
                .thenAccept(material -> {
                    ModelRenderable modelRenderable = ShapeFactory.makeCube(new Vector3((float)length/100, (float)height/100, (float)width/100), new Vector3(0, (float)height/200, 0), material);

                    placeModel(modelRenderable, anchor);
                });

    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode node = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(fragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(modelRenderable);
        fragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }
}
