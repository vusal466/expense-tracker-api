from flask import Flask, request, jsonify
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
import numpy as np

app = Flask(__name__)

training_data = [
    ("mcdonalds", "FOOD"),
    ("burger king", "FOOD"),
    ("kfc chicken", "FOOD"),
    ("wendys", "FOOD"),
    ("subway sandwich", "FOOD"),
    ("dominos pizza", "FOOD"),
    ("starbucks coffee", "FOOD"),
    ("cafe restaurant", "FOOD"),
    ("lunch at restaurant", "FOOD"),
    ("grocery shopping", "FOOD"),
    ("pizza delivery", "FOOD"),
    ("coffee at cafe", "FOOD"),
    ("supermarket food", "FOOD"),
    ("breakfast sandwich", "FOOD"),
    ("dinner with friends", "FOOD"),
    ("kebab house", "FOOD"),
    ("sushi restaurant", "FOOD"),
    ("bakery bread", "FOOD"),
    ("food delivery", "FOOD"),
    ("milk eggs from store", "FOOD"),
    ("taxi to airport", "TRANSPORT"),
    ("uber ride", "TRANSPORT"),
    ("bus ticket", "TRANSPORT"),
    ("metro card", "TRANSPORT"),
    ("fuel petrol", "TRANSPORT"),
    ("car parking fee", "TRANSPORT"),
    ("train ticket", "TRANSPORT"),
    ("flight booking", "TRANSPORT"),
    ("bolt taxi", "TRANSPORT"),
    ("monthly transit pass", "TRANSPORT"),
    ("car wash", "TRANSPORT"),
    ("monthly rent payment", "RENT"),
    ("apartment rent", "RENT"),
    ("house rent", "RENT"),
    ("rent for flat", "RENT"),
    ("room rent", "RENT"),
    ("office rent", "RENT"),
    ("rent deposit", "RENT"),
    ("new shoes nike", "SHOPPING"),
    ("clothes from zara", "SHOPPING"),
    ("amazon purchase", "SHOPPING"),
    ("online shopping", "SHOPPING"),
    ("phone case", "SHOPPING"),
    ("laptop bag", "SHOPPING"),
    ("book purchase", "SHOPPING"),
    ("electronics store", "SHOPPING"),
    ("gift for birthday", "SHOPPING"),
    ("cosmetics makeup", "SHOPPING"),
    ("sports equipment", "SHOPPING"),
    ("electricity bill", "OTHER"),
    ("internet subscription", "OTHER"),
    ("gym membership", "OTHER"),
    ("netflix subscription", "OTHER"),
    ("doctor appointment", "OTHER"),
    ("pharmacy medicine", "OTHER"),
    ("insurance payment", "OTHER"),
    ("bank fee", "OTHER"),
    ("school fee", "OTHER"),
    ("spotify premium", "OTHER"),
]

titles = [d[0] for d in training_data]
labels = [d[1] for d in training_data]

model = Pipeline([
    ("tfidf", TfidfVectorizer(ngram_range=(1, 2), lowercase=True)),
    ("clf", LogisticRegression(max_iter=1000, C=1.0)),
])
model.fit(titles, labels)


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "UP"})


@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(force=True)
    title = data.get("title", "").strip()
    if not title:
        return jsonify({"error": "title is required"}), 400
    predicted = model.predict([title])[0]
    probabilities = model.predict_proba([title])[0]
    confidence = float(np.max(probabilities))
    return jsonify({
        "title": title,
        "category": predicted,
        "confidence": round(confidence, 4),
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)