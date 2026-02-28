//adding security-question
async function addSecurityQuestion(e)
{
    e.preventDefault();
    const form = e.target.form;
    const data =
    {
        question : form.querySelector("#security-question").value,
    }
    try
    {
        const response = await fetch("/api/auth/add-security-question",
        {
            method: "POST",
            headers:
            {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(data),
        });
        const result = response.json();
        console.log(result);
    }
    catch (error)
    {
        console.log(error);
    }
}