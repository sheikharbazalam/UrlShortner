import React, { useState } from "react";
import axios from "axios";

interface UrlResult {
  shortCode: string;
  shortUrl: string;
  originalUrl: string;
}

interface Props {
  onResult: (result: UrlResult) => void;
}

const UrlForm: React.FC<Props> = ({ onResult }) => {
  const [url, setUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await axios.post(
        "http://18.135.28.222:8080/api/shorten",
        { url }
      );
      onResult(response.data);
      setUrl("");
    } catch (err) {
      setError("Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>🔗 URL Shortener</h1>
      <p style={styles.subtitle}>Paste your long URL and get a short one!</p>

      <form onSubmit={handleSubmit} style={styles.form}>
        <input
          type="url"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          placeholder="https://your-long-url.com"
          required
          style={styles.input}
        />
        <button
          type="submit"
          disabled={loading}
          style={styles.button}
        >
          {loading ? "Shortening..." : "Shorten URL"}
        </button>
      </form>

      {error && <p style={styles.error}>{error}</p>}
    </div>
  );
};

const styles: { [key: string]: React.CSSProperties } = {
  container: {
    textAlign: "center",
    padding: "40px 20px",
  },
  title: {
    fontSize: "2.5rem",
    color: "#2d3748",
    marginBottom: "8px",
  },
  subtitle: {
    color: "#718096",
    marginBottom: "32px",
  },
  form: {
    display: "flex",
    gap: "12px",
    justifyContent: "center",
    flexWrap: "wrap",
  },
  input: {
    width: "400px",
    padding: "14px 18px",
    fontSize: "16px",
    border: "2px solid #e2e8f0",
    borderRadius: "8px",
    outline: "none",
  },
  button: {
    padding: "14px 28px",
    fontSize: "16px",
    backgroundColor: "#4f46e5",
    color: "white",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
  },
  error: {
    color: "#e53e3e",
    marginTop: "16px",
  },
};

export default UrlForm;