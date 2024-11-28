package androidsamples.java.tictactoe;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText emailText, passwordText;
    private DatabaseReference userReference;
    private ProgressDialog pd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance("https://boomboomtictactoe-default-rtdb.firebaseio.com/").getReference("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if the user is already logged in on creation
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
            Navigation.findNavController(view).navigate(action);
            return;
        }

        // Initialize the ProgressDialog
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setTitle("Authentication");

        // Initialize UI components after the view is created
        emailText = view.findViewById(R.id.edit_email);
        passwordText = view.findViewById(R.id.edit_password);

        // Setup click listeners
        view.findViewById(R.id.btn_log_in).setOnClickListener(v -> loginUser());
        view.findViewById(R.id.btn_register).setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;

                        Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                        NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                        Navigation.findNavController(requireView()).navigate(action);
                    } else {
                        Toast.makeText(getContext(), "Enter Valid Username or Password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;

                        // Add user to the Realtime Database
                        String uid = user.getUid();
                        userReference.child(uid).child("won").setValue(0);
                        userReference.child(uid).child("lost").setValue(0);

                        Toast.makeText(getContext(), "Registration Successful! New user created.", Toast.LENGTH_SHORT).show();
                        NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                        Navigation.findNavController(requireView()).navigate(action);
                    } else {
                        if (task.getException() != null && task.getException().getMessage().contains("email address is already in use")) {
                            Toast.makeText(getContext(), "Username already exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}