package world;

import java.io.File;
import java.util.List;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModel;
import renderer.Face;
import renderer.Model;
import util.Parser;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class Ground extends Model {

	public Ground(List<Face> f) {
		super(f);
	}

	public Ground(List<Face> f, Vector3f pos) {
		super(f, pos);
	}

	@Override
	protected PhysicsModel setupPhysicsModel(CollisionShape modelShape,
			Vector3f position) {
		System.out.println("InitialPos: " + position);
		// Set up the model in the initial position
        MotionState modelMotionState = new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), 
        		new javax.vecmath.Vector3f(position.x, position.y, position.z), 
        		1)));
                
        RigidBodyConstructionInfo modelConstructionInfo = new RigidBodyConstructionInfo(1.0f, modelMotionState, modelShape);
        
        RigidBody modelRigidBody = new RigidBody(modelConstructionInfo);
        modelRigidBody.setCollisionFlags(CollisionFlags.STATIC_OBJECT);
        modelRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        modelRigidBody.setDamping(0.6f,  0.6f);
        modelRigidBody.setRestitution(0.9f);
        modelRigidBody.setMassProps(0f, new javax.vecmath.Vector3f(0, 0, 0));
        PhysicsModel model = new PhysicsModel(modelShape, 
        		modelRigidBody);
        
        return model;
	}
}
