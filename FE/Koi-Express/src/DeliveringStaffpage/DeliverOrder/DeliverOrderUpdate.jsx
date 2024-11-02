import React, { useEffect, useState } from "react";
import axios from "axios";
import Cookies from "js-cookie";

const decodeToken = (token) => {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload;
  } catch (error) {
    console.error("Failed to decode token:", error);
    return null;
  }
};

const DeliverOrderUpdate = ({ onClose }) => {
  const [fishStatus, setFishStatus] = useState("Khỏe mạnh");
  const [koiQuantity, setKoiQuantity] = useState(0);
  const [distanceFee, setDistanceFee] = useState(0);
  const [commitmentFee, setCommitmentFee] = useState(0);

  const [koiData, setKoiData] = useState({
    KOI_VIET_NAM: [0, 0, 0],
    KOI_NHAT_BAN: [0, 0, 0],
    KOI_CHAU_AU: [0, 0, 0],
  });

  useEffect(() => {
    const token = localStorage.getItem("token");
    const decodedToken = decodeToken(token);
    const staffId = decodedToken?.staffId;

    if (staffId) {
      const sessionKey = `delivering_staff_${staffId}_pickupOrder`;
      const sessionData = sessionStorage.getItem("delivering_staff_16_pickupOrder");

      console.log("Retrieved session data:", sessionData); // Debugging line

      if (sessionData) {
        try {
          const parsedData = JSON.parse(sessionData);
          console.log("Parsed data for koiQuantity:", parsedData.koiQuantity); // Debug line
          setKoiQuantity(parsedData.koiQuantity || 0);
        } catch (error) {
          console.error("Failed to parse session data:", error);
        }
      } else {
        console.warn("No session data found for key:", sessionKey);
      }
    }
  }, []);


  const totalSelectedQuantity =
      koiData.KOI_VIET_NAM.reduce((a, b) => a + b, 0) +
      koiData.KOI_NHAT_BAN.reduce((a, b) => a + b, 0) +
      koiData.KOI_CHAU_AU.reduce((a, b) => a + b, 0);

  const increment = (category, index) => {
    if (totalSelectedQuantity < koiQuantity) {
      setKoiData((prevData) => {
        const updatedCategory = [...prevData[category]];
        updatedCategory[index] += 1;
        return { ...prevData, [category]: updatedCategory };
      });
    }
  };

  const decrement = (category, index) => {
    setKoiData((prevData) => {
      const updatedCategory = [...prevData[category]];
      if (updatedCategory[index] > 0) {
        updatedCategory[index] -= 1;
      }
      return { ...prevData, [category]: updatedCategory };
    });
  };

  const handleSubmit = async () => {
    const koiList = [
      { type: "KOI_VIET_NAM", quantities: koiData.KOI_VIET_NAM },
      { type: "KOI_NHAT_BAN", quantities: koiData.KOI_NHAT_BAN },
      { type: "KOI_CHAU_AU", quantities: koiData.KOI_CHAU_AU },
    ];

    const koiDataPayload = {
      koiList,
      fishStatus,
      koiQuantity,
      distanceFee,
      commitmentFee,
    };

    try {
      const token = localStorage.getItem("token");
      const response = await axios.post(
          "http://localhost:8080/api/orders/calculate-total-fee",
          koiDataPayload,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            withCredentials: true,
          }
      );
      console.log("API response:", response.data);

      // Store the new session data returned from the API in a new cookie
      const calculationSessionKey = `delivering_staff_${decodedToken.staffId}_calculation`;
      Cookies.set(calculationSessionKey, JSON.stringify(response.data.result), { sameSite: "Lax" });
    } catch (error) {
      if (error.response) {
        console.error("Error response data:", error.response.data);
      } else {
        console.error("API call failed:", error.message);
      }
    }

    onClose();
  };

  return (
      <div
          className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
          style={{ zIndex: 1000 }}
      >
        <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-3xl relative">
          <h2 className="mb-4 text-lg font-bold">Chi tiết cá</h2>
          <div className="mb-2">Số lượng: {koiQuantity}</div>

          <div className="grid grid-cols-4 gap-6 p-8 bg-gray-50 rounded-lg shadow-md">
            <div></div>
            <div className="text-center font-semibold text-md border-b pb-2">
              {"< 30 cm"}
            </div>
            <div className="text-center font-semibold text-md border-b pb-2">
              {"30 - 50 cm"}
            </div>
            <div className="text-center font-semibold text-md border-b pb-2">
              {"> 50 cm"}
            </div>

            {["KOI_VIET_NAM", "KOI_NHAT_BAN", "KOI_CHAU_AU"].map((category) => (
                <React.Fragment key={category}>
                  <div className="font-medium text-md border-r pr-4">
                    {category === "KOI_VIET_NAM"
                        ? "Koi VN"
                        : category === "KOI_NHAT_BAN"
                            ? "Koi JP"
                            : "Koi EU"}
                  </div>
                  {koiData[category].map((qty, index) => (
                      <div
                          key={index}
                          className="flex justify-center items-center space-x-3 p-2 border border-gray-200 rounded-md bg-white"
                      >
                        <button
                            onClick={() => decrement(category, index)}
                            className="px-3 py-1 text-gray-700 border border-gray-300 rounded-md"
                        >
                          -
                        </button>
                        <input
                            type="text"
                            value={qty}
                            readOnly
                            className="w-12 text-center border border-gray-300 rounded-md"
                        />
                        <button
                            onClick={() => increment(category, index)}
                            className="px-3 py-1 text-gray-700 border border-gray-300 rounded-md"
                            disabled={totalSelectedQuantity >= koiQuantity}
                        >
                          +
                        </button>
                      </div>
                  ))}
                </React.Fragment>
            ))}
          </div>

          <div className="mt-4">
            <label>Tình trạng cá:</label>
            <select
                value={fishStatus}
                onChange={(e) => setFishStatus(e.target.value)}
                className="w-full p-2 mt-1 border rounded"
            >
              <option>Khỏe mạnh</option>
              <option>Bị thương</option>
              <option>Chết</option>
            </select>
          </div>

          <button
              className="w-full p-2 mt-4 text-white bg-blue-500 rounded"
              onClick={handleSubmit}
          >
            Xác nhận
          </button>
        </div>
      </div>
  );
};

export default DeliverOrderUpdate;
