import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider, signInWithPopup, signOut } from 'firebase/auth';

// Your Firebase configuration from Step 2
const firebaseConfig = {
    apiKey: "AIzaSyCu8fy05YvJrLgpaMC-R2N1QiBJua5zq0A",
    authDomain: "pawfectmatch-2ebca.firebaseapp.com",
    projectId: "pawfectmatch-2ebca",
    storageBucket: "pawfectmatch-2ebca.appspot.app",
    messagingSenderId: "117117751091",
    appId: "1:117117751091:web:844a67a3382c1a43f43846",
    measurementId: "G-TVFZK8Z0NP"
  };

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase Authentication
const auth = getAuth(app);

// Initialize Google Auth Provider
const googleProvider = new GoogleAuthProvider();

// Optional: Force account selection prompt every time
googleProvider.setCustomParameters({ prompt: 'select_account' });

export { auth, googleProvider, signInWithPopup, signOut };