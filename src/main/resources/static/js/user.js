//user-registration-form-method
async function registerUser(e)
{
    e.preventDefault();
    const form = e.target.form;
    const data =
    {
        firstName: form.querySelector("#first-name").value,
        lastName: form.querySelector("#last-name").value,
        phone: form.querySelector("#phone-number").value,
        email: form.querySelector("#e-mail").value,
        passKey: form.querySelector("#pass-code").value,
        securityQuestionId: form.querySelector("#security-question-id").value,
        userSecurityAnswer: form.querySelector("#security-answer").value,
        walletBalance: form.querySelector("#balance").value,
        transactionPin: form.querySelector("#transaction-pin").value,
    };
    try
    {
        const response = await fetch("/api/register-personal",
        {
            method: "POST",
            headers:
            {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(data),
        });
        const result = await response.json();
        console.log(result);
    }
    catch (error)
    {
        console.error(error);
    }
}
