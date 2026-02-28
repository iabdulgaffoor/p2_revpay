//add-role-form-method
async function addRoleForm(e)
{
    e.preventDefault();
    const form = e.target.form;
    const data =
    {
        name : form.querySelector("#role-name").value,
    }
    try
    {
        const response = await fetch("/api/role/add",
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