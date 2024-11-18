import VNPayLogo from "../../assets/images/LogoPayments/VNPay.png";
import Cashbysender from "../../assets/images/LogoPayments/Cashbysender.png";
import Cashbyrep from "../../assets/images/LogoPayments/Cashbyrep.png";

export const nonCashPaymentMethods = [
    {label: "VNPAY", icon: VNPayLogo},
];

export const cashPaymentMethods = [
    {label: "Người gửi trả tiền", icon: Cashbysender},
    {label: "Người nhận trả tiền", icon: Cashbyrep},
];


export const getPaymentMethodIcon = (methodLabel) => {
    const method = paymentMethods.find((m) => m.label === methodLabel);
    return method ? method.icon : null;
};
