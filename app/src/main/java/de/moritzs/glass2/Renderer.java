package de.moritzs.glass2;

/**
 * Created by moritz on 13.06.15.
 */
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.loader.LoaderOBJ;

public class Renderer extends RajawaliRenderer {
    public EngineActivity context;
    public double ang0, ang1;

    private DirectionalLight directionalLight;
    Object3D mBodyObject, mArmObject, mArm2Object;

    Material mGreenMaterial, mRedMaterial;

    int currentMode;
    long modeTimer, submissionTimer, redTime;
    boolean isGreen;
    // mode 0 is from 90 to 0 %
    // mode 1 is at 0 for 2 seconds
    // mode 2 is from 0 to 90


    // we have 2 seconds for mode 0 (after 2 seconds red)
    // stay 2-4 seconds at mode 1 (+-8%). reset counter if outside!
    // we have 2 seconds for mode 2


    public Renderer(EngineActivity context) {
        super(context);
        this.context = context;
        setFrameRate(60);
        ang0=0;
        ang1=0;
        currentMode = 0;
        modeTimer = 0;
        submissionTimer = 10000000000l;
        redTime = 0;
        isGreen = true;
    }
    private void initArmMaterials() {
        mGreenMaterial = new Material();
        mRedMaterial = new Material();

        mGreenMaterial.enableLighting(true);
        mGreenMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        //mGreenMaterial.setColor(new float[]{0,1,0});
        mGreenMaterial.setColor(0xff00ff00);

        mRedMaterial.enableLighting(true);
        mRedMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());

        mRedMaterial.setColor(0xffff0000);
        //mRedMaterial.setColor(new float[]{1,0,0});


    }

    public void initScene(){
        initArmMaterials();

        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.earthtruecolor_nasa_big);
        try{
            material.addTexture(earthTexture);

        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }
        // Load Body
        LoaderOBJ loaderOBJ = new LoaderOBJ(this.context.getResources(), mTextureManager, R.raw.body_obj);
        try {
            loaderOBJ.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        mBodyObject = loaderOBJ.getParsedObject();
        mBodyObject.setMaterial(material);

        getCurrentScene().addChild(mBodyObject);
        mBodyObject.setPosition(0,-1.55,0);
        // Load Arm Green

        LoaderOBJ loaderOBJArm = new LoaderOBJ(this.context.getResources(), mTextureManager, R.raw.arm_obj1);
        try {
            loaderOBJArm.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        mArmObject = loaderOBJArm.getParsedObject();
        //mArmObject.setColor(new Vector3(0,0,0));

        getCurrentScene().addChild(mArmObject);
        mArmObject.setPosition(-0.23,0.94,0);


        // Load Arm Red
        LoaderOBJ loaderOBJArm2 = new LoaderOBJ(this.context.getResources(), mTextureManager, R.raw.arm_obj2);
        try {
            loaderOBJArm2.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        mArm2Object = loaderOBJArm2.getParsedObject();
        //mArmObject.setColor(new Vector3(0,0,0));

        //getCurrentScene().addChild(mArm2Object);
        //mArmObject.setMaterial(mRedMaterial);
        mArm2Object.setPosition(-0.23,0.94,0);
        //mArmObject.rotate(Vector3.Z, 75.0);

        getCurrentCamera().setZ(3.8f);
    }
    public void onTouchEvent(MotionEvent event){
    }
    private void setColor(boolean green) {
        if(green == isGreen)
            return;

        isGreen = green;
        if(isGreen) {
            getCurrentScene().addChild(mArmObject);
            getCurrentScene().removeChild(mArm2Object);
        } else {
            // Add Error counter
            getCurrentScene().addChild(mArm2Object);
            getCurrentScene().removeChild(mArmObject);
        }
    }
    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        if(!isGreen) {
            redTime += deltaTime*1000000000;
        }

        ang0 = context.getRunnable().getAngle();

        switch(currentMode) {
            case 0:
                if(ang0 > -5 && ang0 < 5) {
                    currentMode = 1;
                    modeTimer = elapsedTime;
                    setColor(true);
                } else if(elapsedTime-modeTimer > 2000000000) {
                    setColor(false);
                }
                break;
            case 1:
                if(elapsedTime-modeTimer > 2000000000) {
                    modeTimer = elapsedTime;
                    currentMode = 2;
                } else if(ang0 < -5 || ang0 > 5) {
                    // Reset timer
                    modeTimer = elapsedTime;
                    setColor(false);
                }  else {
                    setColor(true);
                }
                break;
            case 2:
                if(ang0 > 85 && ang0 < 95) {
                    currentMode = 0;
                    modeTimer = elapsedTime;
                    setColor(true);
                } else if(elapsedTime-modeTimer > 2000000000) {
                    setColor(false);
                }
                break;
        }
        if(elapsedTime > submissionTimer) {
            double wrongRatio = (double)redTime/10000000000.0;

            submissionTimer += 10000000000l; // increment 10 seconds.
            redTime = 0;


            //save the last 10 seconds statistics
            context.getSalesForceAccess().pushData(wrongRatio);


        }

        mArmObject.setRotation(-ang0,0,0);
        mArm2Object.setRotation(-ang0,0,0);
        //mArmObject.rotate(Vector3.Axis.Z, ang1);
        //ang0++;

    }


    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){
    }
}