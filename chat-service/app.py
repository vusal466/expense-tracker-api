from flask import Flask, request, jsonify
from langchain_ollama import OllamaLLM
from langchain_core.prompts import PromptTemplate
import requests
import traceback
import os

SPRING_API_URL = os.getenv("SPRING_API_URL", "http://localhost:8081")
OLLAMA_HOST = os.getenv("OLLAMA_HOST", "http://localhost:11434")
app = Flask(__name__)

llm = OllamaLLM(model="qwen2.5:7b", temperature=0.3, base_url=OLLAMA_HOST)

SYSTEM_PROMPT = PromptTemplate(
    input_variables=["expenses", "question"],
    template="""Role: You are an intelligent Financial Assistant.
                Task: Answer the user's question about their expenses based ONLY on the provided Expense Data.

                Instructions:
                1. Normalize Dates: Convert "1 June", "01.06" to "2026-06-01". Convert "June" to "2026-06".
                2. Locate the Answer: Go straight to the "PRE-CALCULATED TOTALS" section at the bottom.
                   - For total spent on a specific day -> check [DAILY TOTALS]
                   - For total spent in a month -> check [MONTHLY TOTALS]
                   - For total spent on a category/product -> check [CATEGORY TOTALS] or [PRODUCT TOTALS]
                   - For the "most expensive" or "highest" expense on a specific day -> check [TOP EXPENSE PER DAY]
                3. STRICT RULE: DO NOT calculate or compare numbers yourself. If multiple items share the highest amount in the pre-calculated text, YOU MUST MENTION ALL OF THEM.
                4. Averages & Savings: Use [STATISTICS & AVERAGES] for average queries. Suggest cutting Non-Essentials (fast food, netflix, etc.) using [CATEGORY TOTALS] or [PRODUCT TOTALS] if asked for financial advice.
                5. Financial Advice (Savings): If the user asks "How can I reduce expenses?" or "What are non-essential expenses?", act as a financial advisor:
                   - Look at the [CATEGORY TOTALS] and [PRODUCT TOTALS].
                   - Categorize items mentally: Essentials (RENT, basic FOOD/grocery, TRANSPORT) vs. Non-Essentials/Wants (e.g., "mcdonalds", "kfc", "coffee", "netflix", "SHOPPING", "OTHER").
                   - Suggest reducing the non-essential items. Mention the EXACT pre-calculated amounts they spent on these non-essentials (e.g., "You spent 90.0 on McDonalds and 15.0 on Netflix"). DO NOT attempt to calculate the total sum of these potential savings yourself. Let the user see the individual amounts.

                Expense Data:
                {expenses}

                User Question: {question}

                Answer:"""
)

chain = SYSTEM_PROMPT | llm


def get_expenses():
    try:
        response = requests.get(f"{SPRING_API_URL}/expenses", timeout=5)
        if response.status_code == 200:
            return response.json()
        return []
    except Exception as e:
        print(f"Error fetching expenses: {e}")
        return []


from collections import defaultdict

def format_expenses(expenses):
    if not expenses:
        return "No expenses found."

    lines = ["DATE | TITLE | AMOUNT | CATEGORY | TYPE"]

    daily_totals = defaultdict(float)
    monthly_totals = defaultdict(float)
    category_totals = defaultdict(float)
    title_totals = defaultdict(float)

    # Günlər üzrə əşyaları yığmaq üçün lüğət
    daily_items = defaultdict(list)

    for e in expenses:
        date_str = e.get('date', 'N/A')
        title = str(e.get('title', 'N/A'))
        category = str(e.get('category', 'N/A')).upper()
        amount = float(e.get('amount', 0))

        # Əsas siyahı
        lines.append(f"{date_str} | {title} | {amount} | {category} | {e.get('type', 'N/A')}")

        if date_str != 'N/A':
            daily_totals[date_str] += amount
            daily_items[date_str].append((amount, f"{title} (Category: {category})"))

            if len(date_str) >= 7:
                monthly_totals[date_str[:7]] += amount

        if category != 'N/A':
            category_totals[category] += amount

        if title != 'N/A':
            title_totals[title.lower()] += amount

    lines.append("\n--- PRE-CALCULATED TOTALS (STRICTLY USE THESE FOR ANSWERS) ---")

    lines.append("\n[DAILY TOTALS]")
    for date, total in daily_totals.items():
        lines.append(f"Date {date}: {total}")

    lines.append("\n[MONTHLY TOTALS]")
    for month, total in monthly_totals.items():
        lines.append(f"Month {month}: {total}")

    lines.append("\n[CATEGORY TOTALS]")
    for cat, total in category_totals.items():
        lines.append(f"Category {cat}: {total}")

    lines.append("\n[PRODUCT TOTALS]")
    for item, total in title_totals.items():
        lines.append(f"Product '{item}': {total}")

    # ƏN BÖYÜK XƏRCLƏRİ PYTHON TAPIR:
    lines.append("\n[TOP EXPENSE PER DAY]")
    for date, items in daily_items.items():
        if items:
            # Ən yüksək məbləği tapır
            max_amount = max(items, key=lambda x: x[0])[0]
            # O məbləğə uyğun gələn BÜTÜN xərclərin adlarını tapır
            top_titles = [item[1] for item in items if item[0] == max_amount]
            # Təkrarları silib cümlə qurur
            titles_str = ", ".join(set(top_titles))
            lines.append(f"Date {date} -> Highest Amount: {max_amount} for item(s): {titles_str}")


    # Bütün xərclərin ümumi cəmi
    total_spent_overall = sum(float(e.get('amount', 0)) for e in expenses)
    # Neçə fərqli ayda xərc edildiyini tapırıq
    months_count = len(monthly_totals) if monthly_totals else 1
    # Aylıq ortalama xərc
    avg_monthly = total_spent_overall / months_count

    lines.append("\n[STATISTICS & AVERAGES]")
    lines.append(f"Total Spent Overall: {total_spent_overall:.2f}")
    lines.append(f"Average Monthly Expense: {avg_monthly:.2f}")

    return "\n".join(lines)



@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "UP"})


@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json(force=True)
    question = data.get("question", "").strip()

    if not question:
        return jsonify({"error": "question is required"}), 400

    expenses = get_expenses()
    formatted = format_expenses(expenses)
    print("DEBUG EXPENSES:", formatted)

    try:
        answer = chain.invoke({
            "expenses": formatted,
            "question": question
        })
        return jsonify({
            "question": question,
            "answer": answer,
            "expense_count": len(expenses)
        })
    except Exception as e:
        traceback.print_exc()
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=False)