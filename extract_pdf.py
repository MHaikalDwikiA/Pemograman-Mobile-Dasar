import sys
from pypdf import PdfReader

def extract_text(pdf_path):
    try:
        reader = PdfReader(pdf_path)
        text = ""
        for i, page in enumerate(reader.pages):
            text += f"\n--- Page {i+1} ---\n"
            text += page.extract_text()
        with open("C:/Users/advan/AndroidStudioProjects/FleetTrackPro/pdf_text.txt", "w", encoding="utf-8") as f:
            f.write(text)
        print("Text extracted successfully to pdf_text.txt")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    extract_text(sys.argv[1])
