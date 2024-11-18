import VNPayLogo from "../../assets/images/LogoPayments/VNPay.png";

export const paymentMethods = [
    {label: "VNPAY", icon: VNPayLogo},
];

export const getPaymentMethodIcon = (methodLabel) => {
    const method = paymentMethods.find((m) => m.label === methodLabel);
    return method ? method.icon : null;
};
