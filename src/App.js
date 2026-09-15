import UrlForm from './components/UrlForm.tsx';
import UrlResult from './components/UrlResult.tsx';
import React, {useState} from 'react';

function App() {
  const [ result, setResult ] = React.useState(null);

  const handleResult = (data) => {
    setResult(data);
  };
  return (
      <div style={{ minHeight: "100vh", backgroundColor: "#eef2ff", padding: "40px 20px" }}>
      <div style={{ maxWidth: "700px", margin: "0 auto", backgroundColor: "white", borderRadius: "16px", padding: "40px", boxShadow: "0 4px 24px rgba(0,0,0,0.08)" }}>
        <UrlForm onResult={handleResult} />
        {result !== null && (
          <UrlResult
            shortCode={result.shortCode}
            shortUrl={result.shortUrl}
            originalUrl={result.originalUrl}
          />
        )}
      </div>
    </div>
  );
}

export default App;
