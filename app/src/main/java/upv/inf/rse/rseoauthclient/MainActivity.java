package upv.inf.rse.rseoauthclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

  private ImageView avatarImageView;
  private TextView nameTextView;
  private TextView emailTextView;

  private LoginButton loginButton;
  private CallbackManager callbackManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    avatarImageView = findViewById(R.id.avatarImageView);
    nameTextView = findViewById(R.id.nameTextView);
    emailTextView = findViewById(R.id.emailTextView);

    loginButton = findViewById(R.id.login_button);

    callbackManager = CallbackManager.Factory.create();
    loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override
      public void onSuccess(LoginResult loginResult) { //se vuelve aqui cuando se ha hecho el login 
        connect(loginResult);
      }
      @Override
      public void onCancel() {
        //mostrar se ha cancelado
      }
      @Override
      public void onError(FacebookException error) {
        //mostrar error de facebook
      }
    });
  }

  private void connect(LoginResult loginResult) {
    AccessToken accessToken = loginResult.getAccessToken();
    // este es el token de acceso que devuelve el SDK de Facebook. Q es el login del usuario.

    GraphRequest request = GraphRequest.newMeRequest(//hace una llamada al se rvidor de autent de fb para q me de el perfil de usr usando wel acces token q he ganado en el login
        accessToken,
        new GraphRequest.GraphJSONObjectCallback() {
          @Override
          public void onCompleted(final JSONObject object, GraphResponse response) {
            final Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
              try {
                String email = object.getString("email");
                Uri avatar = profile.getProfilePictureUri(200, 200);
                String name = profile.getName();

                Picasso.with(getApplicationContext()).load(avatar).into(avatarImageView);
                emailTextView.setText(email);
                nameTextView.setText(name);
              } catch (JSONException e) {
                e.printStackTrace();
              }
            } else {
              //aquí poner error
            }
            Log.v("LoginActivity", response.toString());
          }
        });
    Bundle parameters = new Bundle();
    parameters.putString("fields", "id,name,email");
    request.setParameters(parameters);
    request.executeAsync();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }
}
