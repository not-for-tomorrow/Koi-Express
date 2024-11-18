import React, {useState} from "react";
import axios from "axios";

const DeliverOrderUpdate = ({
                                koiQuantity,
                                koiData,
                                setKoiData,
                                fishStatus,
                                setFishStatus,
                                onClose,
                                onSubmitSuccess,
                            }) => {
    const [errorMessage, setErrorMessage] = useState("");

    const increment = (category, index) => {
        setKoiData((prevData) => {
            const updatedCategory = [...prevData[category]];
            const totalKoiSelected = Object.values(prevData)
                .flat()
                .reduce((acc, qty) => acc + qty, 0);

            if (totalKoiSelected < koiQuantity) {
                updatedCategory[index] += 1;
                setErrorMessage("");
            }
            return {...prevData, [category]: updatedCategory};
        });
    };

    const decrement = (category, index) => {
        setKoiData((prevData) => {
            const updatedCategory = [...prevData[category]];
            if (updatedCategory[index] > 0) {
                updatedCategory[index] -= 1;
                setErrorMessage("");
            }
            return {...prevData, [category]: updatedCategory};
        });
    };

    const handleSubmit = async () => {
        const totalKoiSelected = Object.values(koiData)
            .flat()
            .reduce((acc, qty) => acc + Number(qty), 0);

        if (totalKoiSelected !== koiQuantity) {
            setErrorMessage(
                `Tổng số lượng cá phải là ${koiQuantity}. Hiện tại bạn đã chọn ${totalKoiSelected}.`
            );
            return;
        }

        const koiList = [
            {koiType: "KOI_VIET_NAM", quantity: koiData.KOI_VIET_NAM[0], koiSize: 20, shipmentCondition: fishStatus},
            {koiType: "KOI_VIET_NAM", quantity: koiData.KOI_VIET_NAM[1], koiSize: 40, shipmentCondition: fishStatus},
            {koiType: "KOI_VIET_NAM", quantity: koiData.KOI_VIET_NAM[2], koiSize: 60, shipmentCondition: fishStatus},
            {koiType: "KOI_NHAT_BAN", quantity: koiData.KOI_NHAT_BAN[0], koiSize: 20, shipmentCondition: fishStatus},
            {koiType: "KOI_NHAT_BAN", quantity: koiData.KOI_NHAT_BAN[1], koiSize: 40, shipmentCondition: fishStatus},
            {koiType: "KOI_NHAT_BAN", quantity: koiData.KOI_NHAT_BAN[2], koiSize: 60, shipmentCondition: fishStatus},
            {koiType: "KOI_CHAU_AU", quantity: koiData.KOI_CHAU_AU[0], koiSize: 20, shipmentCondition: fishStatus},
            {koiType: "KOI_CHAU_AU", quantity: koiData.KOI_CHAU_AU[1], koiSize: 40, shipmentCondition: fishStatus},
            {koiType: "KOI_CHAU_AU", quantity: koiData.KOI_CHAU_AU[2], koiSize: 60, shipmentCondition: fishStatus},
        ].filter((item) => item.quantity > 0);

        const koiDataPayload = {koiList};

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

            if (response.data.code === 200) {
                onSubmitSuccess(response.data.result);
                onClose();
            }
        } catch (error) {
            console.error("API call failed:", error.message);
        }
    };

    return (
        <div
            className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
            style={{zIndex: 1000}}
        >
            <div className="relative w-full max-w-3xl p-6 bg-white rounded-lg shadow-lg">
                <button
                    onClick={onClose}
                    className="absolute text-gray-500 top-3 right-3 hover:text-gray-700 focus:outline-none"
                >
                    &#10005;
                </button>

                <h2 className="mb-4 text-lg font-bold">Chi tiết cá</h2>
                <div className="mb-2">Số lượng: {koiQuantity}</div>

                {errorMessage && (
                    <div className="mb-4 text-sm text-red-500">{errorMessage}</div>
                )}

                <div className="grid grid-cols-4 gap-6 p-8 rounded-lg shadow-md bg-gray-50">
                    <div></div>
                    <div className="pb-2 font-semibold text-center border-b text-md">{"< 30 cm"}</div>
                    <div className="pb-2 font-semibold text-center border-b text-md">{"30 - 50 cm"}</div>
                    <div className="pb-2 font-semibold text-center border-b text-md">{"> 50 cm"}</div>

                    {["KOI_VIET_NAM", "KOI_NHAT_BAN", "KOI_CHAU_AU"].map((category) => (
                        <React.Fragment key={category}>
                            <div className="pr-4 font-medium border-r text-md">
                                {category === "KOI_VIET_NAM"
                                    ? "Koi VN"
                                    : category === "KOI_NHAT_BAN"
                                        ? "Koi JP"
                                        : "Koi EU"}
                            </div>
                            {koiData[category].map((qty, index) => (
                                <div
                                    key={index}
                                    className="flex items-center justify-center p-2 space-x-3 bg-white border border-gray-200 rounded-md"
                                >
                                    <button
                                        onClick={() => decrement(category, index)}
                                        className="px-3 py-1 text-gray-700 border border-gray-300 rounded-md"
                                    >
                                        -
                                    </button>
                                    <input
                                        type="text"
                                        value={qty === 0 ? "0" : qty}
                                        min="0"
                                        onChange={(e) =>
                                            handleQuantityChange(category, index, e.target.value)
                                        }
                                        className="w-12 text-center border border-gray-300 rounded-md appearance-none"
                                    />
                                    <button
                                        onClick={() => increment(category, index)}
                                        className="px-3 py-1 text-gray-700 border border-gray-300 rounded-md"
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
                        <option value="HEALTHY">Khỏe mạnh</option>
                        <option value="INJURED">Bị thương</option>
                        <option value="DEAD">Chết</option>
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