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
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener(){
            Intent intent = new Intent(ARMode.this, FrameLayout.class);
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            createCube(hitResult.createAnchor());

        });
    }


    private void createCube(Anchor anchor) {

        MaterialFactory
                .makeOpaqueWithColor(this, new Color(android.graphics.Color.rgb(250, 128, 114)))
                .thenAccept(material -> {
                    ModelRenderable modelRenderable = ShapeFactory.makeCube(new Vector3(0.1f, 0.1f, 0.1f), new Vector3(0, 0, 0), material);

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
