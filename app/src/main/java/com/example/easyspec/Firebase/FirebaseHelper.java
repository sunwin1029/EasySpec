package com.example.easyspec.Firebase;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Data.Users;
import com.example.easyspec.Data.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private List<ProductItem> productItems = new ArrayList<>();
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;

    private FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ProductItems");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public void fetchAllDataFromFirebase(final FirebaseCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productItems.clear();
                for (DataSnapshot category : snapshot.getChildren()) {
                    for (DataSnapshot product : category.getChildren()) {
                        ProductItem item = parseProductItem(product);
                        if (item != null) {
                            productItems.add(item);
                        }
                    }
                }
                callback.onSuccess(productItems);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    private ProductItem parseProductItem(DataSnapshot snapshot) {
        try {
            String name = snapshot.child("name").getValue(String.class);
            int price = snapshot.child("price").getValue(Integer.class);
            int productType = snapshot.child("productType").getValue(Integer.class);

            String company = snapshot.child("Company").getValue(String.class);
            String features = snapshot.child("features").getValue(String.class);

            double totalRating = snapshot.child("rating/totalRating").getValue(Double.class) != null
                    ? snapshot.child("rating/totalRating").getValue(Double.class) : 0.0;
            int ratingCount = snapshot.child("rating/ratingCount").getValue(Integer.class) != null
                    ? snapshot.child("rating/ratingCount").getValue(Integer.class) : 0;

            int IT = snapshot.child("UserNum/IT").getValue(Integer.class) != null
                    ? snapshot.child("UserNum/IT").getValue(Integer.class) : 0;
            int English = snapshot.child("UserNum/English").getValue(Integer.class) != null
                    ? snapshot.child("UserNum/English").getValue(Integer.class) : 0;

            Map<String, List<Review>> reviews = new HashMap<>();
            for (DataSnapshot reviewCategory : snapshot.child("reviews").getChildren()) {
                List<Review> reviewList = new ArrayList<>();
                for (DataSnapshot review : reviewCategory.getChildren()) {
                    String reviewText = review.child("reviewText").getValue(String.class);
                    String userId = review.child("userId").getValue(String.class);
                    int likes = review.child("likes").getValue(Integer.class) != null
                            ? review.child("likes").getValue(Integer.class) : 0;

                    reviewList.add(new Review(reviewText, userId, likes));
                }
                reviews.put(reviewCategory.getKey(), reviewList);
            }

            return new ProductItem(name, price, null, productType, company, features, totalRating,
                    ratingCount, false, reviews, IT, English, 0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void markFavorites(String userId, final FirebaseCallback callback) {
        userReference.child(userId).child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> favorites = new ArrayList<>();
                for (DataSnapshot favorite : snapshot.getChildren()) {
                    favorites.add(favorite.getValue(String.class));
                }

                for (ProductItem item : productItems) {
                    if (favorites.contains(item.getName())) {
                        item.setFavorite(true);
                    }
                }
                callback.onSuccess(productItems);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void fetchAllUsersFromFirebase(final FirebaseUserCallback callback) {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Users> userList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Users user = child.getValue(Users.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                callback.onSuccess(userList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public void addFavoriteItem(String userId, String productName, final FirebaseCallback callback) {
        DatabaseReference favoriteRef = userReference.child(userId).child("favorites");
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> favorites = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot favorite : snapshot.getChildren()) {
                        favorites.add(favorite.getValue(String.class));
                    }
                }

                if (!favorites.contains(productName)) {
                    favorites.add(productName);
                    favoriteRef.setValue(favorites, (error, ref) -> {
                        if (error == null) {
                            callback.onSuccess(null);
                        } else {
                            callback.onFailure(error.toException());
                        }
                    });
                } else {
                    callback.onSuccess(null); // 이미 추가되어 있음
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public void removeFavoriteItem(String userId, String productName, final FirebaseCallback callback) {
        DatabaseReference favoriteRef = userReference.child(userId).child("favorites");
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> favorites = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot favorite : snapshot.getChildren()) {
                        favorites.add(favorite.getValue(String.class));
                    }
                }

                if (favorites.contains(productName)) {
                    favorites.remove(productName);
                    favoriteRef.setValue(favorites, (error, ref) -> {
                        if (error == null) {
                            callback.onSuccess(null);
                        } else {
                            callback.onFailure(error.toException());
                        }
                    });
                } else {
                    callback.onFailure(new Exception("Item not found in favorites."));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public interface FirebaseCallback {
        void onSuccess(List<ProductItem> productItems);
        void onFailure(Exception e);
    }

    public interface FirebaseUserCallback {
        void onSuccess(List<Users> users);
        void onFailure(Exception e);
    }
}
